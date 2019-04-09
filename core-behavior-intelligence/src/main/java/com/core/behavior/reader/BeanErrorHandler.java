package com.core.behavior.reader;

import com.core.behavior.model.File;
import com.core.behavior.model.FileLines;
import com.core.behavior.model.Log;
import com.core.behavior.services.FileLinesService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;

import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.TypeErrorEnum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.beanio.BeanReaderErrorHandler;
import org.beanio.BeanReaderException;
import org.beanio.RecordContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Component
@Data
public class BeanErrorHandler implements BeanReaderErrorHandler {

    private FileLinesService fileLinesService;
    private FileService fileService;
    private LogService logService;

    private List<Log> logs = new ArrayList<>();

    private Long fileId;
    private Map<String, Long>mapErrors;

    public BeanErrorHandler(FileLinesService fileLinesService, LogService logService,FileService fileService) {
        this.fileLinesService = fileLinesService;
        this.logService = logService;
        this.fileService = fileService;
     
    }

    @Override
    public void handleError(BeanReaderException e) throws Exception {

        for (int i = 0; i < e.getRecordCount(); i++) {
            RecordContext recordContext = e.getRecordContext(i);

            boolean hasErrorsField = recordContext.hasFieldErrors();
            boolean hasRecordErrorsField = recordContext.hasRecordErrors();
            long id = 0;
            if (hasErrorsField || hasRecordErrorsField) {
                FileLines fileLines = new FileLines(fileId, StatusEnum.ERROR, recordContext.getRecordText(), (long) recordContext.getLineNumber());
                fileLines = fileLinesService.save(fileLines);
                id = fileLines.getId();
            }

            if (hasRecordErrorsField) {
                Collection<String> errors = recordContext.getRecordErrors();

                for (String error : errors) {
                    Log log = new Log();
                    log.setFieldName(recordContext.getRecordName());
                    log.setFileId(fileId);
                    log.setFileId(id);
                    log.setType(TypeErrorEnum.RECORD);
                    log.setMessageError(error);
                    logs.add(log);

                }
            }
            if (hasErrorsField) {
                Map<String, Collection<String>> errors = recordContext.getFieldErrors();

                for (String key : errors.keySet()) {
                    for (String erro : errors.get(key)) {
                        Log log = new Log();
                        log.setFieldName(key);
                        log.setFileId(fileId);
                        log.setFileLineId(id);
                        log.setType(TypeErrorEnum.COLUMN);
                        log.setMessageError(erro);
                        logs.add(log);
                    }
                }
            }

        }
        logService.saveAll(logs);
        
        if(!logs.isEmpty()){        
            File file = fileService.findById(fileId);
            file.setStatus(StatusEnum.ERROR);
            fileService.saveFile(file);        
        }
    }   
   
}
