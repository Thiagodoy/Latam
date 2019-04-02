package com.core.behavior.services;

import com.core.behavior.model.FileLines;
import com.core.behavior.repository.FileLineRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class FileLinesService {

    @Autowired
    private FileLineRepository fileLineRepository;
    
    @Transactional
    public FileLines save(FileLines line){
        return fileLineRepository.save(line);
    }
    
    
}
