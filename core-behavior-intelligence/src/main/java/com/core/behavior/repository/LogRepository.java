/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Log;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public interface LogRepository extends JpaRepository<Log, Long>{
    
    List<Log> findByFileId(Long fileId);
    Long countByFileId(Long fileId);
}
