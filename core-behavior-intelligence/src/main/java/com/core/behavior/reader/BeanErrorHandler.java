package com.core.behavior.reader;

import com.core.behavior.model.Log;
import com.core.behavior.util.TypeErrorEnum;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

    private List<Log> logs = new ArrayList<>();
    private Long fileId;
    private Map<String,StringBuilder>map = new HashMap<>();

    public BeanErrorHandler() {
    }

    @Override
    public void handleError(BeanReaderException e) throws Exception {

        for (int i = 0; i < e.getRecordCount(); i++) {
            RecordContext recordContext = e.getRecordContext(i);

            boolean hasErrorsField = recordContext.hasFieldErrors();
            boolean hasRecordErrorsField = recordContext.hasRecordErrors();

            if (hasRecordErrorsField) {
                Collection<String> errors = recordContext.getRecordErrors();

                for (String error : errors) {
                    Log log = new Log();
                    String name = recordContext.getRecordName() == null ? "layout" : recordContext.getRecordName();
                    log.setFieldName(name);
                    log.setFileId(fileId);
                    log.setType(TypeErrorEnum.RECORD);
                    log.setMessageError(error);                    
                    log.setRecordContent(recordContext.getRecordText());
                    log.setLineNumber((long) recordContext.getLineNumber());
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
                        log.setRecordContent(recordContext.getRecordText());
                        log.setLineNumber((long) recordContext.getLineNumber());
                        log.setType(TypeErrorEnum.COLUMN);
                        log.setMessageError(erro);
                        logs.add(log);
                    }
                }
            }

        }

        if (e.getRecordCount() == 0) {
            
            Log log = new Log();
            log.setFileId(fileId);
            log.setFieldName("generic");
            log.setType(TypeErrorEnum.RECORD);
            log.setRecordContent(e.getLocalizedMessage());
            log.setLineNumber(0l);
            log.setMessageError(e.getMessage());
            logs.add(log);
        }
    }
    
    private void putValue(String field,String content, String message){
        
        String key = MessageFormat.format("{0}:{1}:{2}", this.fileId,field,content);
        
        if(this.map.containsKey(key)){
            this.map.get(key).append(message + "\n");
        }else{
            this.map.put(key, new StringBuilder(message));
        }
        
    }
}
