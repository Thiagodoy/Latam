package com.core.behavior.services;

import com.core.behavior.model.Ticket;
import com.core.behavior.repository.TicketRepository;
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
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Transactional
    public void saveAll(List<Ticket> list){
        ticketRepository.saveAll(list);
    }
    
    

}
