/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.model.Sequence;
import com.core.behavior.repository.SequenceRepository;
import com.core.behavior.util.SequenceTableEnum;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class SequenceService {
    
    
    @Autowired
    private SequenceRepository sequenceRepository;
    
    @Transactional
    public Sequence getSequence(SequenceTableEnum table, long item){
        
        Sequence sequence = sequenceRepository.findByTable(table);
        Long value = sequence.getSequence() + item;
        sequence.setSequenceMin(value);
        sequence.setSequenceMax(value);        
        sequenceRepository.save(sequence);        
        return sequence;
        
    }
    
    
    
    
    
}
