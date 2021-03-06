/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.model.Agency;
import com.core.behavior.model.Notificacao;
import com.core.behavior.model.TicketError;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.services.NotificacaoService;
import com.core.behavior.services.TicketErrorService;
import com.core.behavior.services.UserActivitiService;
import com.core.behavior.util.LayoutEmailEnum;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
public class FileReturnJob implements Runnable {

    public static final String DATA_FILE_ID = "DATA_FILE_ID";
    public static final String DATA_EMAIL_ID = "DATA_EMAIL_ID";
    private long line;
    private int lineWrited;
    @Autowired
    private ClientAws clientAws;

    @Autowired
    private UserActivitiService userActivitiService;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private TicketErrorService ticketErrorService;

    private SXSSFWorkbook wb;
    private SXSSFSheet sheet;

    private final int LIMIT_ROWS = 10000;

    private Map<String, Object> parameters = new HashMap<>();

    public FileReturnJob(ClientAws clientAws, UserActivitiService userActivitiService, AgencyRepository agencyRepository, FileRepository fileRepository, NotificacaoService notificacaoService) {
        this.clientAws = clientAws;
        this.userActivitiService = userActivitiService;
        this.agencyRepository = agencyRepository;
        this.fileRepository = fileRepository;
        this.notificacaoService = notificacaoService;
    }

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    @Override
    public void run() {

        Long id = (Long) parameters.get(DATA_FILE_ID);
        String emailUser = (String) parameters.get(DATA_EMAIL_ID);

        try {
            
            //Ordena os dados
            List<TicketError> errors = ticketErrorService
                    .findByFileId(id)
                    .parallelStream()
                    .sorted(Comparator.comparing(TicketError::getLine))
                    .collect(Collectors.toList());            
            
            
            final com.core.behavior.model.File file = fileRepository.findById(id).get();
            final Agency agency = agencyRepository.findById(file.getCompany()).get();
            List<List<TicketError>> patitions1 = ListUtils.partition(errors, LIMIT_ROWS);
            List<File> filesCreated = new ArrayList<>();

            Logger.getLogger(FileReturnJob.class.getName()).log(Level.INFO, "[ Files parttions] -> " + patitions1.size());

            
            
            int count = 0;

            while (!patitions1.isEmpty()) {              
                
                
                long startFile = System.currentTimeMillis();
                this.createFile(agency.getLayoutFile());
                this.writeLote1(patitions1.get(0));

                String fileNameNew = file.getName().replaceAll(".(csv|CSV)", "");
                File temp = new File(fileNameNew + "_" + (++count) + "_error.xlsx");

                FileOutputStream fileOut;

                fileOut = new FileOutputStream(temp);
                wb.write(fileOut);
                fileOut.flush();
                fileOut.close();
                wb.dispose();

                filesCreated.add(temp);
                Logger.getLogger(FileReturnJob.class.getName()).log(Level.INFO, "[ Created File -> ] " + (count) + " Tempo -> " + ((System.currentTimeMillis() - startFile) / 1000) + " sec");

            }     
            

            File zipedFile = Utils.zipFiles(file.getName().replaceAll(".(csv|CSV)", ""), file.getVersion(), filesCreated);

            Logger.getLogger(FileReturnJob.class.getName()).log(Level.INFO, "[ File ZIP  -> ]" + zipedFile.getName());

            uploadFileReturn(zipedFile, agency);
            this.createNotification(file.getName(), zipedFile.getName(), emailUser, agency);
            filesCreated.add(zipedFile);

            this.deleteFiles(filesCreated);
            
            System.gc();

        } catch (Exception ex) {
            Logger.getLogger(FileReturnJob.class.getName()).log(Level.SEVERE, "[ executeInternal ]", ex);
        }
    }

    private void createFile(long layout) {

        String sheetName = "Erros";

        wb = new SXSSFWorkbook();
        wb.setCompressTempFiles(true);

        sheet = wb.createSheet(sheetName);

        CellStyle cellBold = wb.createCellStyle();

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(true);

        String[] header = layout == 1 ? Utils.headerMinLayoutFile.split(";") : Utils.headerFullLayoutFile.split(";");
        SXSSFRow rr = sheet.createRow(0);
        cellBold.setFont(font);

        //Escreve o header do arquivo
        for (int i = 0; i < header.length; i++) {
            SXSSFCell cell = rr.createCell(i);
            cell.setCellValue(header[i]);
        }

        Font fontRecord = wb.createFont();
        fontRecord.setCharSet(XSSFFont.ANSI_CHARSET);

        CellStyle style = wb.createCellStyle();
        style.setFont(fontRecord);

        this.line = sheet.getLastRowNum();

    }

    private void writeLote1(List<TicketError> errors) {

        long start = System.currentTimeMillis();
        CellStyle backgroundStyle = wb.createCellStyle();
        backgroundStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        backgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font fontRecord = wb.createFont();
        fontRecord.setCharSet(XSSFFont.ANSI_CHARSET);

        CellStyle style = wb.createCellStyle();
        style.setFont(fontRecord);

        this.lineWrited = sheet.getLastRowNum();

        while (!errors.isEmpty()) {

            TicketError ticketError = errors.remove(0);

            SXSSFRow row = sheet.createRow(++this.lineWrited);

            //iterating c number of columns
            String[] values = ticketError.getContent().split("\\[col\\]");
            for (int c = 0; c < values.length; c++) {
                SXSSFCell cell = row.createCell(c);
                cell.setCellStyle(style);

                String v = values[c].equals("[empty]") ? "" : values[c];
                cell.setCellValue(v);

                CellReference cellReference = new CellReference(cell);
                CellAddress currentCellAddress = new CellAddress(cellReference);

                Optional<String> comments = ticketError.hasComments(currentCellAddress);

                //Adicionando comentarios                
                if (comments.isPresent()) {
                    Comment com = createCellComment(sheet, "Behavior", comments.get(), cell);
                    cell.setCellComment(com);
                    com.setAddress(cell.getAddress());                    
                    cell.setCellStyle(backgroundStyle);
                }
            }
        }
        Logger.getLogger(FileReturnJob.class.getName()).log(Level.INFO, "[ writeLote ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }   
 

    private void createNotification(String fileName, String fileNameReturn, String emailUser, Agency agency) {
        Notificacao notificacao = new Notificacao();
        notificacao.setLayout(LayoutEmailEnum.NOTIFICACAO_FILE_RETURN);

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":email", emailUser);        
        String link = "https://api.dataquality.behaviorintelligence.com.br/file/download/arquivo-retorno?company=" + String.valueOf(agency.getId()) + "&fileName=" + fileNameReturn;
        parameter.put(":link", link);
        String nameUser = userActivitiService.getUser(emailUser).getFirstName();
        parameter.put(":nome", Utils.replaceAccentToEntityHtml(nameUser));
        parameter.put(":arquivo", fileName);

        notificacao.setParameters(Utils.mapToString(parameter));
        notificacaoService.save(notificacao);

    }

    private void deleteFiles(List<File> files) {

        for (File file : files) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException ex) {
                Logger.getLogger(FileReturnJob.class.getName()).log(Level.SEVERE, "[ deleteFiles ]", ex);
            }
        }
    }

    private void uploadFileReturn(File file, Agency agency) throws IOException {
        String folder = agency.getS3Path().split("\\\\")[1];
        clientAws.uploadFileReturn(file, folder);
    }    

    private static Comment createCellComment(SXSSFSheet sheet, String author, String comment, SXSSFCell cell) {

        // comments only supported for XLSX
        CreationHelper factory = sheet.getWorkbook().getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();

        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex() + 10);
        anchor.setRow1(cell.getRowIndex());
        anchor.setRow2(cell.getRowIndex() + 15);

        Comment cmt = drawing.createCellComment(anchor);

        RichTextString str = factory.createRichTextString(comment);
        cmt.setString(str);
        cmt.setAuthor(author);
        return cmt;

    }

}
