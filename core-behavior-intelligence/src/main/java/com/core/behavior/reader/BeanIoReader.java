package com.core.behavior.reader;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.jobs.ProcessFileJob2;
import com.core.behavior.model.FileLines;
import com.core.behavior.model.Log;
import com.core.behavior.services.FileLinesService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.util.StatusEnum;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class BeanIoReader {

    private BeanErrorHandler beanErrorHandler;

    @Autowired
    private FileService fileService;

    @Autowired
    private LogService logService;

    @Autowired
    private FileLinesService fileLinesService;

    public <T> Optional<T> parse(File file,com.core.behavior.model.File f, String str, String xmlParser, String company, String user) {

        beanErrorHandler = new BeanErrorHandler(fileLinesService, logService, fileService);
        BeanReader reader = null;
        T record = null;        
        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream stream = factory.getClass().getClassLoader().getResourceAsStream(xmlParser);
            factory.load(stream);

            reader = factory.createReader(str, file);

            long totalLines = this.countLineNumber(file);            
            f.setQtdTotalLines(totalLines);                                    
            fileService.saveFile(f);
            
            beanErrorHandler.setFileId(f.getId());
            reader.setErrorHandler(beanErrorHandler);

            record = (T) reader.read();
            FileParsedDTO dto = (FileParsedDTO) record;
            dto.setFile(f);

        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
            f.setStatus(StatusEnum.ERROR);
            f = fileService.saveFile(f);
        }

        reader.close();

        return Optional.ofNullable(record);
    }

    private void generateStatusFile(List<Log>logs){
        
        
        
        
        
        
        
    }    
    
    private long countLineNumber(File file) {

        long count = 0;

        try {
            FileReader reader = new FileReader(file);
            LineNumberReader readerLine = new LineNumberReader(reader);

            while (readerLine.readLine() != null) {
                count++;
            }

            return count;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProcessFileJob2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProcessFileJob2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0l;
    }

}
