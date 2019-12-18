/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.annotations.PositionColumnExcel;
import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.dto.LineErrorDTO;
import com.core.behavior.dto.LogDTO;
import com.core.behavior.model.Agency;
import com.core.behavior.model.Notificacao;
import com.core.behavior.model.TicketError;
import com.core.behavior.properties.BehaviorProperties;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.services.NotificacaoService;
import com.core.behavior.services.TicketErrorService;
import com.core.behavior.services.UserActivitiService;
import com.core.behavior.util.LayoutEmailEnum;
import com.core.behavior.util.MessageErrors;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

public class FileReturnJob1 implements Runnable {

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

    
    public FileReturnJob1(ClientAws clientAws,UserActivitiService userActivitiService,AgencyRepository agencyRepository,FileRepository fileRepository,NotificacaoService notificacaoService){
        this.clientAws = clientAws;
        this.userActivitiService = userActivitiService;
        this.agencyRepository = agencyRepository;
        this.fileRepository = fileRepository;
        this.notificacaoService = notificacaoService;
    }
    
    public void setParameter(String key, Object value){
        this.parameters.put(key, value);
    }
    
    @Override
    public void run()  {

        Long id = (Long) parameters.get(DATA_FILE_ID);
        String emailUser = (String) parameters.get(DATA_EMAIL_ID);

        try {
            //List<LogDTO> logsDTO = this.getData(id);
            
            List<TicketError> errors = ticketErrorService.findByFileId(id);
            
            final com.core.behavior.model.File file = fileRepository.findById(id).get();
            final Agency agency = agencyRepository.findById(file.getCompany()).get();

           // List<LineErrorDTO> errors = this.prepareData(logsDTO);
           // logsDTO = null;

           List<List<LineErrorDTO>> patitions = ListUtils.partition(null, LIMIT_ROWS);
            List<File> filesCreated = new ArrayList<>();

            Logger.getLogger(FileReturnJob1.class.getName()).log(Level.INFO, "[ Files parttions -> ]" + patitions.size());

            int count = 0;

            for (List<LineErrorDTO> partition : patitions) {

                Logger.getLogger(FileReturnJob1.class.getName()).log(Level.INFO, "[ Create File -> ] " + (1 + count));
                long startFile = System.currentTimeMillis();
                this.createFile(agency.getLayoutFile());
                this.writeLote(null);

                String fileNameNew = file.getName().replaceAll(".(csv|CSV)", "");
                File temp = new File(fileNameNew + "_" + (++count) + "_error.xlsx");

                FileOutputStream fileOut;

                fileOut = new FileOutputStream(temp);
                wb.write(fileOut);
                fileOut.flush();
                fileOut.close();
                wb.dispose();

                filesCreated.add(temp);
                Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ Created File -> ] " + (count) + " Tempo -> " + ((System.currentTimeMillis() - startFile) / 1000) + " sec");

            }

            
            File zipedFile = Utils.zipFiles(file.getName().replaceAll(".(csv|CSV)", ""), file.getVersion(), filesCreated);

            Logger.getLogger(FileReturnJob1.class.getName()).log(Level.INFO, "[ File ZIP  -> ]" + zipedFile.getName());

            uploadFileReturn(zipedFile, agency);
            this.createNotification(file.getName(), zipedFile.getName(), emailUser, agency);
            filesCreated.add(zipedFile);

            this.deleteFiles(filesCreated);

        } catch (Exception ex) {
            Logger.getLogger(FileReturnJob1.class.getName()).log(Level.SEVERE, "[ executeInternal ]", ex);
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

    private void writeLote(List<TicketError> errors) {

        long start = System.currentTimeMillis();
        CellStyle backgroundStyle = wb.createCellStyle();
        backgroundStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        backgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font fontRecord = wb.createFont();
        fontRecord.setCharSet(XSSFFont.ANSI_CHARSET);

        CellStyle style = wb.createCellStyle();
        style.setFont(fontRecord);
        

        this.lineWrited = sheet.getLastRowNum();

        for (int r = 0; r < errors.size(); r++) {
            SXSSFRow row = sheet.createRow(++this.lineWrited);

            //iterating c number of columns
            String[] values = errors.get(r).getContent().split("\\[col\\]");
            for (int c = 0; c < values.length; c++) {
                SXSSFCell cell = row.createCell(c);
                cell.setCellStyle(style);
                
                String v = values[c].equals("[empty]") ? "" : values[c]; 
                
                cell.setCellValue(v);
                CellReference cellReference = new CellReference(cell);
                CellAddress currentCellAddress = new CellAddress(cellReference);

//                String value = comments.remove(currentCellAddress.formatAsString());

                //Adicionando comentarios                
//                if (value != null) {
//                    Comment com = createCellComment(sheet, "Behavior", value, cell);
//                    cell.setCellComment(com);
//                    com.setAddress(cell.getAddress());
//                    //cell.setCellStyle(borderStyle);
//                    cell.setCellStyle(backgroundStyle);
//                }

            }
        }
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ writeLote ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }

    private List<LineErrorDTO> prepareData(List<TicketError> logs) {

        long start = System.currentTimeMillis();        

        List<LineErrorDTO> e = new ArrayList<LineErrorDTO>();
        LineErrorDTO.reset();
        
        List<Field> fields = Arrays.asList(TicketError.class.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(PositionColumnExcel.class)).collect(Collectors.toList());
        

        logs.parallelStream().forEach(l -> {
            LineErrorDTO error = new LineErrorDTO(l.getContent(), l.getLine());
            ConcurrentHashMap<String,String> messages = MessageErrors.getMessages();
              fields.stream().forEach(f->{
              
                  try {
                    Long value = f.getLong(l);
                    
                    if(value.equals(1L)){
                        String colum = f.getAnnotation(PositionColumnExcel.class).column();
                      //  error.
                    }
                    
                  } catch (Exception ex) {
                  }
                  
                  
              });
            
            synchronized (e) {
                e.add(error);
            }
        });

        List<LineErrorDTO> eSorted = e.parallelStream().sorted(Comparator.comparing(LineErrorDTO::getLineNumber)).collect(Collectors.toList());

        int possition = 1;
        for (LineErrorDTO lineErrorDTO : eSorted) {
            
            if (possition > LIMIT_ROWS) {
                possition = 1;
            }
            
            lineErrorDTO.setLine(++possition);           
        }

        Map<String, Map<String, String>> messages = new HashMap<>();    
      
        

        

       

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

        return eSorted;
    }

    private void createNotification(String fileName, String fileNameReturn, String emailUser, Agency agency) {
        Notificacao notificacao = new Notificacao();
        notificacao.setLayout(LayoutEmailEnum.NOTIFICACAO_FILE_RETURN);

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":email", emailUser);

        //:FIXME Colocar o endereço no arquivo de configurações
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
                Logger.getLogger(FileReturnJob1.class.getName()).log(Level.SEVERE, "[ deleteFiles ]", ex);
            }
        }
    }

    private void uploadFileReturn(File file, Agency agency) throws IOException {
        String folder = agency.getS3Path().split("\\\\")[1];
        clientAws.uploadFileReturn(file, folder);
    }

    private Map<String, String> appendComments(List<LineErrorDTO> errors) {
        Map<String, String> comments = new HashMap<String, String>();
        errors.forEach(dto -> {
            dto.getComments().forEach((key, message) -> {
                if (comments.containsKey(key)) {
                    String value = comments.get(key) + "\n" + message;
                    comments.put(key, value);
                } else {
                    comments.put(key, message);
                }
            });
        });

        //Limpa as referencias para GC
        errors.parallelStream().forEach(e -> {
            e.getComments().clear();
        });

        return comments;
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
