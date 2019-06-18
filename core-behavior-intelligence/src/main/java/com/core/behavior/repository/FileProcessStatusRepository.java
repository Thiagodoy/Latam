/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.dto.FileStatusDTO;
import com.core.behavior.model.FileProcessStatus;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface FileProcessStatusRepository extends JpaRepository<FileProcessStatus, Long>{    
    
    @Query(nativeQuery = true)
    List<FileStatusDTO>getProcessStatus(@Param("fileId")Long fileId);
    List<FileProcessStatus>findByFileId(Long fileId);
    
    @Modifying   
    @Transactional
    @Query(value = "delete from file_status  where file_id = :fileId", nativeQuery = true)  
    void deleteByFileId(@Param("fileId") Long fileId);
    
}
