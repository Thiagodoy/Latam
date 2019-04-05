package com.core.behavior.services;

import com.core.behavior.model.Ticket;
import com.core.behavior.repository.TicketRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class TicketService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    @Transactional
    public List<Ticket> saveAll(List<Ticket> list) {
        return ticketRepository.saveAll(list);
    }

    @Transactional
    public <T extends Ticket> List<T> bulkSave(Collection<T> entities) {
        final List<T> savednotEntities = new ArrayList<T>(entities.size());
        int i = 0;
        for (T t : entities) {
            try{
               entityManager.persist(t);
           
        }catch(Exception e){      
           
           savednotEntities.add(t);
        }
            i++;
            if (i % 1000 == 0) {
                // Flush a batch of inserts and release memory.
                entityManager.flush();
                entityManager.clear();
            }
        }
        
         System.out.println("registros repetidos ->" + savednotEntities.size());
        
        return savednotEntities;
    }

    private <T extends Ticket> T persistOrMerge(T t) {
        
        try{
               entityManager.persist(t);
            return t;
        }catch(Exception e){
        
           
            return null;
        }
//        if (true) {
//            entityManager.persist(t);
//            return t;
//        } else {
//            return entityManager.merge(t);
//        }
    
    }

}
