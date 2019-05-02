/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.File;
import com.core.behavior.util.StatusEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@Repository
public interface FileRepository extends JpaRepository<File, Long> , JpaSpecificationExecutor<File>{
    
    List<File> findByStatus(StatusEnum status);
    Optional<File> findByNameAndCompany(String name, Long company);
}
