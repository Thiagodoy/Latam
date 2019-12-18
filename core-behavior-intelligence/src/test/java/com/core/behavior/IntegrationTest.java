package com.core.behavior;

import com.core.behavior.jobs.TicketCupomValidationJob;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.IntegrationService;
import com.core.behavior.services.TicketService;
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
    
//    @Test
//    public void contextLoads() throws SchedulerException, InterruptedException, Exception {
//        
//        List<Ticket> tickets = service.listByFileId(100L);
//        
//        integrationService.integrate(tickets);
//        
//    }
    
//    @Test
//    public void sendfile() throws SchedulerException, InterruptedException, Exception {
//        
//        integrationService.openConnection();
//        
//        java.io.File file = integrationService.makeFile();
//        integrationService.sendEmail(file);
//        
//        integrationService.closeConnection();
//        
//    }
    
    //@Test
    public void verifyCupom() throws SchedulerException, InterruptedException, Exception {
        
        Ticket ticket = new Ticket();
        ticket.setAgrupamentoA("0006AZBPZDD16012019RIBEIRO/AMAND");
        
         //Thread t = new Thread(new TicketCupomValidationJob(service,ticket));
        
//         t.start();
//         
//         while(t.isAlive()){}
         
         
    }
}
