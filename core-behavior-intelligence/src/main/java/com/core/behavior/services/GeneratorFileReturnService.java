/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.dto.LineErrorDTO;
import com.core.behavior.model.Log;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.repository.LogRepository;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.core.behavior.repository.FileIntegrationRepository;
import java.util.Comparator;

/**
 *
 * @author thiag
 */
@Service
public class GeneratorFileReturnService {

    @Autowired
    private LogRepository logRepository;
    
    @Autowired
    private AgencyRepository agencyRepository;
    

    @Autowired
    private FileRepository fileRepository;

    public File generateFileReturnFriendly(Long id) throws IOException {

        final List<Log> logs = logRepository.findByFileId(id);
        final com.core.behavior.model.File file = fileRepository.findById(id).get();
        final String fileName = file.getName();
        
        final long layout = agencyRepository.findById(file.getCompany()).get().getLayoutFile();
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
        
        return generateFile(e, layout);
    }

    private File generateFile(List<LineErrorDTO> errors, long layout) throws FileNotFoundException, IOException {

        String sheetName = "Erros";

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);

        CellStyle backgroundStyle = wb.createCellStyle();

        CellStyle cellBold = wb.createCellStyle();

        backgroundStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        backgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle borderStyle = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(true);


        String[] header = layout == 1 ? Utils.headerMinLayoutFile.split(";") : Utils.headerFullLayoutFile.split(";");;
        XSSFRow rr = sheet.createRow(0);
        cellBold.setFont(font);

        for (int i = 0; i < header.length; i++) {
            XSSFCell cell = rr.createCell(i);
            cell.setCellValue(header[i]);
        }

        for (int r = 0; r < errors.size(); r++) {
            XSSFRow row = sheet.createRow(r + 1);

            //iterating c number of columns
            String[] values = errors.get(r).getLineContent().split(";");
            for (int c = 0; c < values.length; c++) {
                XSSFCell cell = row.createCell(c);
                cell.setCellValue(values[c]);

            }
        }

        //Realiza append para comentarios da mesma Celula
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

        //Create a comments
        comments.forEach((key, value) -> {            
            CellReference cellReference = new CellReference(key);
            XSSFRow row = sheet.getRow(cellReference.getRow());

            XSSFCell cell = row.getCell(cellReference.getCol()) != null ? row.getCell(cellReference.getCol()) : row.createCell(cellReference.getCol());

            Comment com = createCellComment(sheet, "Behavior", value, cell);
            cell.setCellComment(com);
            com.setAddress(cell.getAddress());
            cell.setCellStyle(borderStyle);
            cell.setCellStyle(backgroundStyle);

        });

        File temp = File.createTempFile("temp", ".xlsx");
        FileOutputStream fileOut = new FileOutputStream(temp);

        //write this workbook to an Outputstream.
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        return temp;
    }

    private static Comment createCellComment(XSSFSheet sheet, String author, String comment, XSSFCell cell) {

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
