package com.core.behavior.services;

import com.core.behavior.model.Ticket;
import com.core.behavior.repository.TicketRepository;
import java.sql.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class TicketService {    

    @Autowired()    
    private TicketRepository ticketRepository;

   

    @Transactional()
    public List<Ticket> saveAll(List<Ticket> list) {
        return ticketRepository.saveAll(list);
    }

    public long checkLong(Long value){
        return value != null ? value : 0;
    }
    
    public double checkDouble(Double value){
        return value != null ? value : 0.0;
    }
    
    public Date checDate(java.util.Date value){
        return value != null ? new Date(value.getTime()) : null;
    }

}
