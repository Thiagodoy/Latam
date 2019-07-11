/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.dto.LineErrorDTO;
import com.core.behavior.model.Log;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.repository.LogRepository;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

/**
 *
 * @author thiag
 */
@Service
public class GeneratorFileReturnService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private FileRepository fileRepository;

    public File generateFileReturnFriendly(Long id) throws IOException {

        final List<Log> logs = logRepository.findByFileId(id);
        final String fileName = fileRepository.findById(id).get().getName();

        final List<Log> logDistinct = logs.stream().distinct().collect(Collectors.toList());

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

        //e.stream().sorted(Comparator.comparingInt(LineErrorDTO::getLine));
        return generateFile(e, fileName);
    }

    private File generateFile(List<LineErrorDTO> errors, String fileName) throws FileNotFoundException, IOException {

        String sheetName = "Erros";//name of sheet

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

//        borderStyle.setBorderBottom(BorderStyle.MEDIUM_DASH_DOT_DOT);
//        borderStyle.setBottomBorderColor(IndexedColors.BLUE1.getIndex());
//        borderStyle.setBorderLeft(BorderStyle.MEDIUM);
//        borderStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        borderStyle.setBorderRight(BorderStyle.MEDIUM);
//        borderStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        borderStyle.setBorderTop(BorderStyle.MEDIUM);
//        borderStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        //iterating r number of rows
        String[] header = Utils.headerMinLayoutFile.split(";");
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

        //Create a comments
        for (LineErrorDTO e : errors) {

            e.getComments().forEach((key, value) -> {
                CellReference cellReference = new CellReference(key);
                XSSFRow row = sheet.getRow(cellReference.getRow());
                
                XSSFCell cell = row.getCell(cellReference.getCol()) != null ? row.getCell(cellReference.getCol()) : row.createCell(cellReference.getCol());

                Comment com = createCellComment(sheet, "Behavior", value, cell);
                cell.setCellComment(com);
                com.setAddress(cell.getAddress());
                cell.setCellStyle(borderStyle);
                cell.setCellStyle(backgroundStyle);

            });

        }

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
