package com.core.behavior.services;

import com.core.behavior.model.Log;
import com.core.behavior.repository.LogRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class LogService {
    
    @Autowired
    private LogRepository logRepository;
    
    @Transactional
    public List<Log> findByLineId(Long lineId){
       
        return logRepository
                .findByFileLineId(lineId)
                .stream()
                .sorted((a,b)->a.getId().compareTo(b.getId()))
                .collect(Collectors.toList());
        
    }  
    
    @Transactional
    public Log saveLog(Log log){
        return logRepository.save(log);
    }
     
    @Transactional
    public void saveAll(List<Log> log){
         logRepository.saveAll(log);
    }
    
    
    @Transactional
    public void logGeneric(long fileId,String message){
        Log log = new Log();
        log.setMessageError(message);
        log.setFileId(fileId);
        logRepository.save(log);
    }
    
}
