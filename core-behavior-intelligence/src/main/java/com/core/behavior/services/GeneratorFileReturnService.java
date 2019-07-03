/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.activiti.specifications.Comments;
import com.core.behavior.dto.FileRecordError;
import com.core.behavior.model.Log;
import com.core.behavior.repository.LogRepository;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class GeneratorFileReturnService {

//    @Autowired
//    private LogRepository logRepository;
//
//    public File getFileReturn(Long id) throws IOException {
//
//       List<Log> logs = logRepository.findByFileId(id);
//       
//       List<Log> logDistinct = logs.stream().distinct().collect(Collectors.toList());
//       
//       String[]colunas = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T"};
//       
//       int line = 1;
//       
//        LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();
//       
//        for (Log log : logDistinct) {
//            
//            FileRecordError error = new FileRecordError();
//            
//            
//            List<String>contentValues =  Arrays.asList(log.getRecordContent().split(";"));
//            
//            for (int i = 0; i < colunas.length; i++) {
//                map.put(colunas[i] + line, contentValues.get(i));
//            }
//            
//            error.setValues(map);
//            
//            
//            
//            for (Log l : logs) {
//                long index = Utils.layoutMin.indexOf(l.getFieldName());
//                
//                Comments comment = new Comments();
//                comment.setRow(line);
//                comment.setString(new HSSFRichTextString(l.getMessageError()));
//                comment.setVisible(true);
//                comment.setAuthor("Behavior");
//                comment.setAddress(colunas);
//                
//            }
//            
//            ++line;
//        }    
//        
//        
//        
//        
//        
//        
//        
//       
//       
//       
//
//        File fileIn = null;
//        File fileout = File.createTempFile("temp", ".xls");
//        HSSFWorkbook workbook = null;
//
//        try {
//
//            //fileIn = createFile(url);
//            workbook = new HSSFWorkbook();
//            HSSFSheet sheet = workbook.createSheet("Erros");
//            
//
//        } catch (Exception e) {
//
//        }
//
//        return null;
//    }
//
//    @SuppressWarnings("unused")
//    private static void populateCell2(HSSFSheet sheet, Map<String, Object> values, boolean isRecursive, boolean makeShift) {
//
//        //Sempre utilizar LinkedHashMap no values para garantir a ordem de inserção
//        Set<String> keys = values.keySet();
//        HSSFRow oldRow = null;
//        String keyOld = null;      
//
//        // Insere linhas
//        if (makeShift) {
//            makeShift(values, sheet);
//        }
//
//        for (String key : keys) {
//            // Logger.info(key);
//            CellReference cellReference = new CellReference(key);
//            HSSFRow row = sheet.getRow(cellReference.getRow());
//
//            // Caso os dados são recursivo copia a linha anterior
//            if (isRecursive && keyOld != null && !keyOld.equals(key.substring(1))) {
//                if (row == null)
//					;
//                row = sheet.createRow(cellReference.getRow());
//
//                for (int i = 0; i < oldRow.getLastCellNum(); i++) {
//                    HSSFCell cellOld = oldRow.getCell(i);
//                    if (cellOld != null) {
//                        HSSFCell cellNew = row.createCell(i);
//                    }
//                }
//
//                // Busca por linhas possui merge
//                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
//                    CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
//                    if (cellRangeAddress.getFirstRow() == oldRow.getRowNum()) {
//                        CellRangeAddress newCellRangeAddress = new CellRangeAddress(row.getRowNum(), (row.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())), cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
//                        try {
//                            sheet.addMergedRegion(newCellRangeAddress);
//                        } catch (Exception e) {
//                            /* WORKROUND:NAO APLICA NA MESMA LINHA DE ORIGEM */
//                        }
//
//                    }
//                }
//
//            }
//
//            HSSFCell cell = row.getCell(cellReference.getCol());
//            Object value = values.get(key);
//           
//            cell.setCellValue(String.valueOf(value));
//            
//            
//            keyOld = key.substring(1);
//            oldRow = row;
//        }
//    }
//
//    private static void makeShift(Map<String, Object> values, HSSFSheet sheet) {
//
//        String[] keys = values.keySet().toArray(new String[]{});
//
//        int indexInit = Integer.parseInt(keys[0].substring(1));
//        int indexEnd = Integer.parseInt(keys[keys.length - 1].substring(1));
//
//        CellReference cellReference = new CellReference(keys[0]);
//
//        HSSFRow row = sheet.getRow(cellReference.getRow());
//        int size = (indexEnd + 1) - indexInit;
//        sheet.shiftRows(row.getRowNum() + 1, sheet.getLastRowNum(), size);
//
//    }

}
