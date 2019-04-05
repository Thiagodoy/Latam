package com.core.behavior.services;

import com.core.behavior.model.FileLines;
import com.core.behavior.repository.FileLineRepository;
import com.core.behavior.util.StatusEnum;
import java.util.List;
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
    
    @Transactional
    public void saveAll(List<FileLines> list){
        fileLineRepository.saveAll(list);
    }
    
    public boolean hasLinesWithErrors(Long idFile){
       return ! this.findLinesWithErrors(idFile).isEmpty();
    }
    
    public List<FileLines> findLinesWithErrors(Long idFile){
    
        return this.listLines(idFile, StatusEnum.ERROR);
    }
    
    @Transactional
    public void removeLinesProcessed(Long idFile){
        List<FileLines> lines = fileLineRepository.findByFileIdAndStatus(idFile, StatusEnum.SUCCESS);
        fileLineRepository.deleteAll(lines);
    }
    
    private List<FileLines> listLines(Long fileId, StatusEnum status){
        return fileLineRepository.findByFileIdAndStatus(fileId, status);
    }
    
}
