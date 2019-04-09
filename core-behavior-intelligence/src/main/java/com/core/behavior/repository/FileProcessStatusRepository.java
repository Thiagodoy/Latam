/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.dto.FileStatusDTO;
import com.core.behavior.model.FileStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface FileProcessStatusRepository extends JpaRepository<FileStatus, Long>{    
    
    @Query(nativeQuery = true)
    List<FileStatusDTO>getProcessStatus(@Param("fileId")Long fileId);
    List<FileStatus>findByFileId(Long fileId);
    
}
