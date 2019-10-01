/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.dto.LineErrorDTO;
import com.core.behavior.dto.LogDTO;
import com.core.behavior.model.Agency;
import com.core.behavior.model.Notificacao;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.services.LogService;
import com.core.behavior.services.NotificacaoService;
import com.core.behavior.services.UserActivitiService;
import com.core.behavior.util.LayoutEmailEnum;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
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
    private long line;
    private int lineWrited;

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private LogService logRepository;

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

    private SXSSFWorkbook wb;
    private SXSSFSheet sheet;

    private void createFile(long layout, String fileName) {

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

    private void writeLote(List<LineErrorDTO> errors) {

        long start = System.currentTimeMillis();
        CellStyle backgroundStyle = wb.createCellStyle();
        backgroundStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        backgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font fontRecord = wb.createFont();
        fontRecord.setCharSet(XSSFFont.ANSI_CHARSET);

        CellStyle style = wb.createCellStyle();
        style.setFont(fontRecord);

        Map<String, String> comments = this.appendComments(errors);

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ writeLote ]  getLastRowNum -> " + sheet.getLastRowNum());

        this.lineWrited = sheet.getLastRowNum();

        for (int r = 0; r < errors.size(); r++) {
            SXSSFRow row = sheet.createRow(++this.lineWrited);

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
                    //.setCellStyle(borderStyle);
                    //cell.setCellStyle(backgroundStyle);
                }

            }
        }
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ writeLote ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }

    private List<LineErrorDTO> prepareData(List<LogDTO> logs) {

        long start = System.currentTimeMillis();

        final List<LogDTO> logDistinct = logs.parallelStream().distinct().collect(Collectors.toList());
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData ] -> etapa 1");
        List<LineErrorDTO> e = new ArrayList<LineErrorDTO>();
        LineErrorDTO.reset();

        logDistinct.parallelStream().forEach(l -> {
            LineErrorDTO error = new LineErrorDTO(l.getRecord_content(), l.getLine_number());
            synchronized (e) {
                e.add(error);
            }

        });

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData ] -> etapa 2");

        List<LineErrorDTO> eSorted = e.parallelStream().sorted(Comparator.comparing(LineErrorDTO::getLineNumber)).collect(Collectors.toList());

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData ] -> etapa 3");

        eSorted.forEach(l -> {
            l.setLine((int) ++this.line);
        });

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData map ] -> etapa 4");

        Map<String, Map<String, String>> messages = new HashMap<>();

        logs.forEach(mm -> {
            if (messages.containsKey(mm.getRecord_content())) {
                Map<String, String> mapfield = messages.get(mm.getRecord_content());

                if (mapfield.containsKey(mm.getField_name())) {
                    String m = mapfield.get(mm.getField_name()) + "\n" + mm.getMessage_error();
                    mapfield.put(mm.getField_name(), m);
                } else {
                    mapfield.put(mm.getField_name(), mm.getMessage_error());
                }

            } else {
                Map<String, String> mapfield = new HashMap<String, String>();
                mapfield.put(mm.getField_name(), mm.getMessage_error());
                messages.put(mm.getRecord_content(), mapfield);
            }
        });

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData ] -> etapa 5");
        eSorted.forEach(ee -> {
            if (messages.containsKey(ee.getLineContent())) {
                Map<String, String> mapfield = messages.get(ee.getLineContent());

                mapfield.forEach((field, value) -> {
                    final String column = Utils.getPositionExcelColumn(field);
                    ee.put(column, value);
                });
            }

            messages.remove(ee.getLineContent());
//            uu.forEach(lll -> {
//                final String column = Utils.getPositionExcelColumn(lll.field_name);
//                ee.put(column, lll.getMessage_error());
//            });
        });

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData ] -> etapa 6");
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

        return eSorted;
    }

    private void createNotification(String fileName, String fileNameReturn, String emailUser, Agency agency) {
        Notificacao notificacao = new Notificacao();
        notificacao.setLayout(LayoutEmailEnum.NOTIFICACAO_FILE_RETURN);

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":email", emailUser);

        //:FIXME Colocar o endereço no arquivo de configurações
        String link = "http://10.91.0.146:8001/file/download/arquivo-retorno?company=" + String.valueOf(agency.getId()) + "&fileName=" + fileNameReturn;

        parameter.put(":link", link);

        String nameUser = userActivitiService.getUser(emailUser).getFirstName();

        parameter.put(":nome", Utils.replaceAccentToEntityHtml(nameUser));
        parameter.put(":arquivo", fileName);

        notificacao.setParameters(Utils.mapToString(parameter));
        notificacaoService.save(notificacao);

    }

    private List<LogDTO> getData(Long id) {

        try {

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileReturnJob.class.getName()).log(Level.SEVERE, null, ex);
            }

            Dataset<Row> jdbcDF = sparkSession.sqlContext().read()
                    .format("jdbc")
                    .option("url", "jdbc:mysql://hmg-application.csczqq5ovcud.us-east-1.rds.amazonaws.com:3306/behavior?rewriteBatchedStatements=true&useTimezone=true&serverTimezone=UTC&useLegacyDatetimeCode=false")
                    .option("dbtable", "behavior.log")
                    //.option("numPartitions", "10")
                    .option("user", "behint_hmg")
                    .option("password", "Beh1ntHmg_App#2018")
                    .load();

            Dataset<LogDTO> logs = jdbcDF.as(Encoders.bean(LogDTO.class));

            long start = System.currentTimeMillis();
            Dataset<LogDTO> logsOfFile = logs.where("file_id = " + String.valueOf(id));
            System.out.println("[ Buscando ] QTD -> " + logsOfFile.count());
            System.out.println("[ Buscando ] Tempo -> " + (System.currentTimeMillis() - start) / 1000);

            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ Start parse ]");
            List<LogDTO> list = new ArrayList<>();//logsOfFile.collectAsList();//Collections.synchronizedList(new ArrayList<>());
            Iterator<LogDTO> it = logsOfFile.javaRDD().toLocalIterator();
            
            while(it.hasNext()){
                list.add(it.next());
            }
            
//
////        logsOfFile.foreachPartition((itrtr) -> {
////
////            List<LogDTO> temp = new ArrayList<>();
////
////            while (itrtr.hasNext()) {
////                temp.add(itrtr.next());
////            }
////
////            synchronized (list) {
////                list.addAll(temp);
////            }
////
////        });
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ End parse ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

            return list;

        } catch (Exception ex) {
            Logger.getLogger(FileReturnJob.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ArrayList<LogDTO>();

    }

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        Long id = jec.getJobDetail().getJobDataMap().getLong(DATA_FILE_ID);
        String emailUser = jec.getJobDetail().getJobDataMap().getString(DATA_EMAIL_ID);

        // try {
        this.getData(id);

//        PageRequest page = PageRequest.of(0, 500000, Sort.by("lineNumber").ascending());
//        // Page<Log> pageResponse = logRepository.findDistinct(id, page);
//        Stream<Log> pageResponse = logRepository.listDistinctByFileId(id);
//
//        List<LineErrorDTO> e = Collections.synchronizedList(new ArrayList<>());
//        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
//        long start = System.currentTimeMillis();
//        pageResponse.parallel().forEach(l -> {
//
//            final LineErrorDTO error = new LineErrorDTO(l.getRecordContent(), l.getLineNumber());
//
//            executorService.submit(() -> {
//
//                List<Log> j = logRepository.listByRecordContent(id, error.getLineContent());
//
//                j.parallelStream().forEach(lll -> {
//                    final String column = Utils.getPositionExcelColumn(lll.fieldName);
//                    error.put(column, lll.getMessageError());
//                });
//
//                e.add(error);
//
//            });
//
//        });
//
//        executorService.shutdown();
//        //Aguarda o termino do processamento
//        while (!executorService.isTerminated()) {
//        }
//
//        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ prepareData ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
//
//            final com.core.behavior.model.File file = fileRepository.findById(id).get();
//            final Agency agency = agencyRepository.findById(file.getCompany()).get();
//
//            List<LineErrorDTO> errors = this.prepareData(logsDTO);
//            logsDTO = null;
//
//            List<List<LineErrorDTO>> patitions = ListUtils.partition(errors, 10000);
//            List<File> filesCreated = new ArrayList<>();
//
//            int count = 0;
//
//            for (List<LineErrorDTO> patition : patitions) {
//
//                this.createFile(agency.getLayoutFile(), file.getName());
//                this.writeLote(errors);
//
//                String fileNameNew = file.getName().replaceAll(".(csv|CSV)", "");
//                File temp = new File(fileNameNew + "_" + (++count) + "_error.xlsx");
//
//                FileOutputStream fileOut;
//
//                fileOut = new FileOutputStream(temp);
//                wb.write(fileOut);
//                fileOut.flush();
//                fileOut.close();
//                wb.dispose();
//
//                filesCreated.add(temp);
//
//                this.createNotification(file.getName(), temp.getName(), emailUser, agency);
//
//            }
//
//           File zipedFile = Utils.zipFiles(file.getName().replaceAll(".(csv|CSV)", ""), filesCreated);
//           
//           uploadFileReturn(zipedFile,agency);
//           filesCreated.add(zipedFile);
//           
//           this.deleteFiles(filesCreated);
//           
//           
//
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(FileReturnJob.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(FileReturnJob.class.getName()).log(Level.SEVERE, null, ex);
//        }
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
