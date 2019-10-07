/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.dto.LogStatusSinteticoDTO;
import com.core.behavior.model.Log;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public interface LogRepository extends JpaRepository<Log, Long>{
    
    
    Page<Log> findByFileId(Long fileId,Pageable page);
    
    
    List<Log> findByRecordContentAndFileId(String content, Long fileId);
    
   // Stream<Log> findByFileId(Long fileId,Pageable page);
    
    Optional<Log> findByFileIdAndFieldName(Long fileId,String fieldName);
    Long countByFileId(Long fileId);
    
    @Query(nativeQuery = true)
    List<LogStatusSinteticoDTO>listErroSintetico(@Param("fileId")Long fileId, @Param("fieldName")String fieldName);
    
    
    @Modifying 
    @Transactional
    @Query(value = "delete from log  where file_id = :fileId", nativeQuery = true)    
    void deleteByFileId(@Param("fileId")Long fileId);
    
    
    
   
}
