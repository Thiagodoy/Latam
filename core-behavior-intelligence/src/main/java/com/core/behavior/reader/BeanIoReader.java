package com.core.behavior.reader;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.model.FileLines;
import com.core.behavior.services.FileLinesService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.util.StatusEnum;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public <T> Optional<T> parse(File file, String str, String xmlParser, String company, String user) {

        beanErrorHandler = new BeanErrorHandler(fileLinesService, logService, fileService);
        BeanReader reader = null;
        T record = null;
        com.core.behavior.model.File f = null;
        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream stream = factory.getClass().getClassLoader().getResourceAsStream(xmlParser);
            factory.load(stream);

            reader = factory.createReader(str, file);

            f = new com.core.behavior.model.File();
            f.setCompany(company);
            f.setName(file.getName());
            f.setUserId(user);
            f.setStatus(StatusEnum.PROCESSING);
            f.setLines(new ArrayList<FileLines>());
            f.setCreatedDate(LocalDateTime.now());

            f = fileService.saveFile(f);
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

}
