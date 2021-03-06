package com.core.behavior.io;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.dto.TicketDTO;
import com.core.behavior.exception.ApplicationException;

import com.core.behavior.services.FileService;
import com.core.behavior.util.MessageCode;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.beanio.BeanReader;
import org.beanio.InvalidRecordException;
import org.beanio.RecordContext;
import org.beanio.StreamFactory;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

public class BeanIoReader {

    private BeanErrorHandler beanErrorHandler;    
    private FileService fileService;    
    private final String ENCODING = "ISO-8859-1";

    
    
    public BeanIoReader(FileService fileService){
        this.fileService = fileService; 
    }
    
    public BeanIoReader(){        
    }
    
    
    public <T> Optional<T> parse(File file, com.core.behavior.model.File f, com.core.behavior.util.Stream stream) {

        long start = System.currentTimeMillis();
        long end;

        beanErrorHandler = new BeanErrorHandler();
        BeanReader reader = null;
        T record = null;
        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream str = factory.getClass().getClassLoader().getResourceAsStream(stream.getStreamFile());

            factory.load(str);

            Reader rr = new InputStreamReader(new FileInputStream(file), ENCODING);
            reader = factory.createReader(stream.getStreamId(), rr);

            long totalLines = this.countLineNumber(file);
            f.setQtdTotalLines(totalLines);
            fileService.saveFile(f);

            beanErrorHandler.setFileId(f.getId());
            reader.setErrorHandler(beanErrorHandler);

            record = (T) reader.read();

            end = System.currentTimeMillis();
            fileService.setParseTime(f.getId(), (end - start) / 1000);
        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
            f.setStatus(StatusEnum.VALIDATION_ERROR);
            f = fileService.saveFile(f);
        }finally{        
            if(reader != null){
                reader.close();
            }           
        }

        

        if (record instanceof FileParsedDTO) {
            FileParsedDTO pa = (FileParsedDTO) record;

            long count = 2;
            for (TicketDTO t : pa.getTicket()) {
                t.setLineFile(String.valueOf(count++));
            }

        }

        return Optional.ofNullable(record);
    }

    public static long countLineNumber(File file) {
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
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
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

    public void headerIsValid(File file, com.core.behavior.util.Stream stream) throws Exception {

        FileReader reader = null;
        LineNumberReader readerLine = null;
        beanErrorHandler = new BeanErrorHandler();
        List<String> errors = null;

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

            Reader rr = new InputStreamReader(new FileInputStream(file), ENCODING);
            BeanReader beanReader = factory.createReader(stream.getStreamId(), rr);

            beanReader.read();
            beanReader.close();

            FileUtils.forceDelete(fileHeader);

        } catch (InvalidRecordException ex) {
            int count = ex.getRecordCount();
            errors = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                RecordContext context = ex.getRecordContext(i);

                Map<String, Collection<String>> map = context.getFieldErrors();

                for (String key : context.getFieldErrors().keySet()) {
                    for (String object : context.getFieldErrors(key)) {
                        errors.add(object);
                        Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, object);
                    }
                }
            }

            FileUtils.forceDelete(file);

        } catch (Exception e) {
            throw e;

        } finally {
            try {
                readerLine.close();
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, ex.getMessage());
            }

        }

        if (Optional.ofNullable(errors).isPresent()) {
            this.generateFileHeaderReturn(errors);
        }

    }

    private void generateFileHeaderReturn(List<String> errors) throws ApplicationException {

        String message = errors.stream().map(e -> MessageFormat.format("<p style=\"font-size: 15px; font-weight: bold;\">{0}</p>", e)).collect(Collectors.joining("\n"));

        throw new ApplicationException(MessageCode.FILE_HEADER_INVALID, message);

    }

}
