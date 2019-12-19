/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.dto.FileStatusProcessDTO;
import com.core.behavior.dto.FileLinesApprovedDTO;
import com.core.behavior.model.File;
import com.core.behavior.util.StatusEnum;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@Repository
public interface FileRepository extends JpaRepository<File, Long> , JpaSpecificationExecutor<File>{
    
    List<File> findByStatus(StatusEnum status);
    
    @Query(nativeQuery = true)
    List<FileStatusProcessDTO> statusProcesss(@Param("agencia")Long agencia, @Param("start")LocalDateTime start, @Param("end")LocalDateTime end);
    
    @Query(nativeQuery = true)
    Optional<FileLinesApprovedDTO> moveToAnalitics(@Param("file") Long id);   
    
    Optional<File> findByNameAndCompany(String name, Long company);
    
    List<File>findByCompanyAndCreatedDateBetween(Long company,LocalDateTime start, LocalDateTime end);
    
    
    
}
