/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Sequence;
import com.core.behavior.util.SequenceTableEnum;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author thiag
 */
public interface SequenceRepository extends JpaRepository<Sequence, Long> {



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from sequence where table = :table")
    Sequence findByTable(@Param("table")SequenceTableEnum table);


    
}
