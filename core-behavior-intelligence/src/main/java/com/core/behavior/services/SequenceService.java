/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.model.Sequence;
import com.core.behavior.repository.SequenceRepository;
import com.core.behavior.util.SequenceTableEnum;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    public void resetSequence(SequenceTableEnum table) {

        Sequence sequence = sequenceRepository.findByTable(table);        
        sequence.setSequenceMin(0L);
        sequence.setVersion(0L);
        
        sequenceRepository.save(sequence);

    }

    @Transactional
    public Sequence getSequence(SequenceTableEnum table, long item) throws Exception {

        int count = 0;
        do {

            try {

                //Sequence sequence =  em.find(Sequence.class, 1l, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                Sequence sequence = sequenceRepository.findByTable(table);
                Long value = sequence.getSequence() + item;
                sequence.setSequenceMin(value);
                sequence.setSequenceMax(value);
                sequenceRepository.save(sequence);
                return sequence;

            } catch (Exception e) {
                Logger.getLogger(SequenceService.class.getName()).log(Level.SEVERE, "[getSequence]", e);
                if (count > 3) {
                    throw e;
                }
            }

            count++;
        } while (count <= 3);

        return null;
    }

}
