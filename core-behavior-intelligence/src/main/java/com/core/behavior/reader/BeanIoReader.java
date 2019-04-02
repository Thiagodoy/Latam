package com.core.behavior.reader;

import com.core.behavior.model.File;
import com.core.behavior.model.FileLines;
import com.core.behavior.model.Log;
import com.core.behavior.services.FileLinesService;
import com.core.behavior.services.LogService;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.TypeErrorEnum;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.beanio.BeanReader;
import org.beanio.BeanReaderException;
import org.beanio.StreamFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class BeanIoReader {

    
    @Autowired
    private LogService logService;
    
    @Autowired
    private FileLinesService fileLinesService;
    
    public <T> List<T> parse(File file, String str, String xmlParser, boolean isHeader) {

        BeanReader reader = null;
        List<T> list = new ArrayList<T>();
        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream stream = factory.getClass().getClassLoader().getResourceAsStream(xmlParser);
            factory.load(stream);

            java.io.File f = mountFile(file, isHeader);
            reader = factory.createReader(str, f);

            T record = null;

            while ((record = (T) reader.read()) != null) {
                list.add(record);
            }
        } catch (BeanReaderException e) {

            long lineNumber = isHeader ? e.getRecordContext().getLineNumber() + 1 : e.getRecordContext().getLineNumber();

            FileLines fileLines = file.getLines().stream().filter((f) -> f.getLineNumber() == lineNumber).findFirst().orElse(null);

            boolean hasErrorsField = e.getRecordContext().hasFieldErrors();
            boolean hasRecordErrorsField = e.getRecordContext().hasRecordErrors();

            if(hasRecordErrorsField){
                Collection<String> errors = e.getRecordContext().getRecordErrors();
                
                for(String error :errors){
                    Log log = new Log();
                        log.setFieldName(e.getRecordContext().getRecordName());
                        log.setFileId(file.getId());
                        log.setFileId(fileLines.getId());
                        log.setType(TypeErrorEnum.RECORD);
                        log.setMessageError(error);
                        logService.saveLog(log);
                }
            }
            if (hasErrorsField) {
                Map<String, Collection<String>> errors = e.getRecordContext().getFieldErrors();
              
                for (String key : errors.keySet()) {
                    for(String erro : errors.get(key)){
                        Log log = new Log();
                        log.setFieldName(key);
                        log.setFileId(file.getId());
                        log.setFileLine(fileLines.getId());
                        log.setType(TypeErrorEnum.COLUMN);
                        log.setMessageError(erro);
                        logService.saveLog(log);
                    }
                }
            }
            
            fileLines.setStatus(StatusEnum.ERROR);
            fileLinesService.save(fileLines);
            
         
        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        reader.close();

        return list;
    }

    private java.io.File mountFile(File file, boolean isHeader) throws IOException {

        java.io.File fileTemp = java.io.File.createTempFile("arquivo", "tmp");
        FileWriter writer = new FileWriter(fileTemp);

        String separator = System.getProperty("line.separator");
        List<FileLines> lines;

        if (isHeader) {
            lines = file.getLines().stream().filter((l) -> l.getLineNumber() == 0).collect(Collectors.toList());
        } else {
            lines = file.getLines().stream().filter((l) -> l.getLineNumber() > 0).collect(Collectors.toList());
        }

        for (FileLines line : lines) {
            writer.write(line.getContent() + separator);
        }

        writer.close();

        return fileTemp;

    }

}
