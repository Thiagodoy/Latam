/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.File;
import com.core.behavior.util.StatusEnum;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@Repository
public interface FileRepository extends CrudRepository<File, Long>{
    
    List<File> findByStatus(StatusEnum status);
}
