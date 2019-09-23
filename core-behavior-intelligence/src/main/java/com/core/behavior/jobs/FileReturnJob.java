/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.dto.LineErrorDTO;
import com.core.behavior.model.Agency;
import com.core.behavior.model.Log;
import com.core.behavior.model.Notificacao;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.repository.LogRepository;
import com.core.behavior.services.NotificacaoService;
import com.core.behavior.services.UserActivitiService;
import com.core.behavior.util.LayoutEmailEnum;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author thiag
 */
@DisallowConcurrentExecution
public class FileReturnJob extends QuartzJobBean {

    public static final String DATA_FILE_ID = "DATA_FILE_ID";
    public static final String DATA_EMAIL_ID = "DATA_EMAIL_ID";

    @Autowired
    private LogRepository logRepository;

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

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        Long id = jec.getJobDetail().getJobDataMap().getLong(DATA_FILE_ID);
        String emailUser = jec.getJobDetail().getJobDataMap().getString(DATA_EMAIL_ID);

        long start = System.currentTimeMillis();
        final List<Log> logs = logRepository.findByFileId(id);
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ LOAD LOGS ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

        final com.core.behavior.model.File file = fileRepository.findById(id).get();
        final Agency agency = agencyRepository.findById(file.getCompany()).get();

        final long layout = agency.getLayoutFile();
        Comparator lineNumber = Comparator.comparingLong(Log::getLineNumber);
        final List<Log> logDistinct = logs.stream().distinct().collect(Collectors.toList());
        logDistinct.sort(lineNumber);
        List<LineErrorDTO> e = new LinkedList<LineErrorDTO>();
        LineErrorDTO.reset();

        logDistinct.stream().forEach(l -> {

            LineErrorDTO error = new LineErrorDTO(l.getRecordContent());

            logs.stream().filter(ll -> ll.getRecordContent().equals(l.getRecordContent())).forEach(lll -> {

                final String column = Utils.getPositionExcelColumn(lll.fieldName);
                error.put(column, lll.getMessageError());

            });
            e.add(error);
        });

        File fileTemp = null;
        try {
            
            start = System.currentTimeMillis();
            fileTemp = this.generateFile(e, layout, file.getName());            
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ MAKE XLSX ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
            
            this.uploadFileReturn(fileTemp, agency);

            Notificacao notificacao = new Notificacao();
            notificacao.setLayout(LayoutEmailEnum.NOTIFICACAO_FILE_RETURN);

            Map<String, String> parameter = new HashMap<String, String>();
            parameter.put(":email", emailUser);

            String link = "http://10.91.0.146:8001/file/download/arquivo-retorno?company=" + String.valueOf(agency.getId()) + "&fileName=" + fileTemp.getName();

            parameter.put(":link", link);

            String nameUser = userActivitiService.getUser(file.getUserId()).getFirstName();

            parameter.put(":nome", Utils.replaceAccentToEntityHtml(nameUser));
            parameter.put(":arquivo", fileTemp.getName());

            notificacao.setParameters(Utils.mapToString(parameter));
            notificacaoService.save(notificacao);

        } catch (Exception ex) {
            Logger.getLogger(FileReturnJob.class.getName()).log(Level.SEVERE, "[executeInternal]", ex);
        } finally {

            try {

                if (fileTemp != null) {
                    FileUtils.forceDelete(fileTemp);
                }

            } catch (IOException ex) {
                Logger.getLogger(FileReturnJob.class.getName()).log(Level.SEVERE, "[executeInternal]", ex);
            }
        }

    }

    private void uploadFileReturn(File file, Agency agency) throws IOException {
        String folder = agency.getS3Path().split("\\\\")[1];
        clientAws.uploadFileReturn(file, folder);
    }

    private File generateFile(List<LineErrorDTO> errors, long layout, String fileName) throws FileNotFoundException, IOException {

        String sheetName = "Erros";

        SXSSFWorkbook wb = new SXSSFWorkbook();

        SXSSFSheet sheet = wb.createSheet(sheetName);

        CellStyle backgroundStyle = wb.createCellStyle();

        CellStyle cellBold = wb.createCellStyle();

        backgroundStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        backgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle borderStyle = wb.createCellStyle();

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

        Map<String, String> comments = this.appendComments(errors);

        for (int r = 0; r < errors.size(); r++) {
            SXSSFRow row = sheet.createRow(r + 1);

            //iterating c number of columns
            String[] values = errors.get(r).getLineContent().split("\\[col\\]");
            for (int c = 0; c < values.length; c++) {
                SXSSFCell cell = row.createCell(c);
                //cell.setCellStyle(style);
                cell.setCellValue(values[c]);
                CellReference cellReference = new CellReference(cell);
                CellAddress currentCellAddress = new CellAddress(cellReference);

                String value = comments.remove(currentCellAddress.formatAsString());

                //Adicionando comentarios                
                if (value != null) {
                    Comment com = createCellComment(sheet, "Behavior", value, cell);
                    cell.setCellComment(com);
                    com.setAddress(cell.getAddress());
                    cell.setCellStyle(borderStyle);
                    cell.setCellStyle(backgroundStyle);
                }

            }
        }

        String fileNameNew = fileName.replaceAll(".(csv|CSV)", "");

        File temp = new File(fileNameNew + "_error.xlsx");
        FileOutputStream fileOut = new FileOutputStream(temp);

        //write this workbook to an Outputstream.
        wb.write(fileOut);
        wb.dispose();
        fileOut.flush();
        fileOut.close();
        return temp;
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

        //Limpa as eferencias para GC
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