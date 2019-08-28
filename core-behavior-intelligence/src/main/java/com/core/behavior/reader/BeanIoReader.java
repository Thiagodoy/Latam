package com.core.behavior.reader;

import com.core.behavior.dto.HeaderDTO;
import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.util.StatusEnum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
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

    public <T> Optional<T> parse(File file, com.core.behavior.model.File f, com.core.behavior.util.Stream stream) {

        long start = System.currentTimeMillis();
        long end;

        Logger.getLogger(BeanIoReader.class.getName()).log(Level.INFO, "ENCODE -> " + System.getProperty("file.encoding"));
        System.out.println("charsets -> " + Charset.availableCharsets().toString());

        beanErrorHandler = new BeanErrorHandler();
        BeanReader reader = null;
        T record = null;
        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream str = factory.getClass().getClassLoader().getResourceAsStream(stream.getStreamFile());

            factory.load(str);

            Reader rr = new InputStreamReader(new FileInputStream(file), "ISO-8859-1");
            reader = factory.createReader(stream.getStreamId(), rr);
            
            

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
            fileService.setParseTime(f.getId(), (end - start) / 1000);
        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
            f.setStatus(StatusEnum.VALIDATION_ERROR);
            f = fileService.saveFile(f);
        }

        reader.close();

        return Optional.ofNullable(record);
    }

    private long countLineNumber(File file) {
        long count = 0;
        FileReader reader = null;
        LineNumberReader readerLine = null;

        try {
            reader = new FileReader(file);
            readerLine = new LineNumberReader(reader);

            while (readerLine.readLine() != null) {
                count++;
            }

            return --count;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                readerLine.close();
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return 0l;
    }

    public boolean headerIsValid(File file, com.core.behavior.util.Stream stream) {

        FileReader reader = null;
        LineNumberReader readerLine = null;
        beanErrorHandler = new BeanErrorHandler();

        try {
            reader = new FileReader(file);
            readerLine = new LineNumberReader(reader);

            String header = readerLine.readLine();
            File fileHeader = File.createTempFile("fileHeader", ".csv");
            FileWriter writer = new FileWriter(fileHeader);
            writer.write(header);
            writer.flush();
            writer.close();

            StreamFactory factory = StreamFactory.newInstance();
            InputStream str = factory.getClass().getClassLoader().getResourceAsStream(stream.getStreamFile());

            factory.load(str);

            BeanReader beanReader = factory.createReader(stream.getStreamId(), file);

            HeaderDTO headerDto = (HeaderDTO) beanReader.read();
            beanReader.close();

            FileUtils.forceDelete(fileHeader);

            return Optional.ofNullable(headerDto).isPresent();

        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, ex.getMessage());
            return false;
        } finally {
            try {
                readerLine.close();
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, ex.getMessage());
            }

        }

    }

}
