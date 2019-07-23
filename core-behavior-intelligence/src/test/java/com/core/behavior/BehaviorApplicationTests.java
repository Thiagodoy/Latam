package com.core.behavior;

import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BehaviorApplicationTests {

    @Autowired
    private TicketService service;
    
	@Test
	public void contextLoads() {
            
            List<Ticket> a = new  ArrayList<Ticket>();
            
            for (int i = 0; i < 10; i++) {
                Ticket t = new  Ticket();
                t.setFileId(400l);
                a.add(t);
            }
            
        try {
            service.saveBatch(a);
        } catch (SQLException ex) {
            Logger.getLogger(BehaviorApplicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

}
