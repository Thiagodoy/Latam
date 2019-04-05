/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.FileLines;
import com.core.behavior.util.StatusEnum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface FileLineRepository extends JpaRepository<FileLines, Long> {
    
    
    List<FileLines> findByFileIdAndStatus(Long idFile, StatusEnum status);
    
    
}
