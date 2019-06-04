package com.core.behavior.reader;

import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.util.StatusEnum;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
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

    public <T> Optional<T> parse(File file, com.core.behavior.model.File f, String str, String xmlParser, String user) {

        long start = System.currentTimeMillis();
        long end;
        
        beanErrorHandler = new BeanErrorHandler();
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

            if (beanErrorHandler.getLogs().size() > 0) {
                logService.saveBatch(beanErrorHandler.getLogs());
            }
            end = System.currentTimeMillis();
            fileService.setParseTime(f.getId(), (end - start)/1000);
        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
            f.setStatus(StatusEnum.VALIDATION_ERROR);
            f = fileService.saveFile(f);
            logService.logGeneric(f.getId(), ex.getLocalizedMessage());
        }

        reader.close();

        return Optional.ofNullable(record);
    }

    private long countLineNumber(File file) {

        long count = 0;

        try {
            FileReader reader = new FileReader(file);
            LineNumberReader readerLine = new LineNumberReader(reader);

            while (readerLine.readLine() != null) {
                count++;
            }

            return --count;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0l;
    }

}
