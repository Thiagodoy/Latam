/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Sequence;
import com.core.behavior.util.SequenceTableEnum;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author thiag
 */
public interface SequenceRepository extends JpaRepository<Sequence, Long> {



    //@Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)    
    Sequence findByTable(SequenceTableEnum table);


    
}
