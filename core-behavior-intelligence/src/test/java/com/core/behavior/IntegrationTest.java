package com.core.behavior;

import com.core.behavior.model.Ticket;
import com.core.behavior.services.IntegrationService;
import com.core.behavior.services.TicketService;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationTest {
    
    @Autowired
    private TicketService service;
    
    @Autowired
    private IntegrationService integrationService;
    
    @Test
    public void contextLoads() throws SchedulerException, InterruptedException, Exception {
        
        List<Ticket> tickets = service.listByFileId(100L);
        
        integrationService.integrate(tickets);
        
    }
    
}
