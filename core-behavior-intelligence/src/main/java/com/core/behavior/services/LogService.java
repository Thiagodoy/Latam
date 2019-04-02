package com.core.behavior.services;

import com.core.behavior.model.File;
import com.core.behavior.model.Log;
import com.core.behavior.repository.LogRepository;
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
    public void gravaLogHeader(File file){
       
        
        
    }  
    
    @Transactional
    public Log saveLog(Log log){
        return logRepository.save(log);
    }
    
}
