/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior;

import com.core.behavior.model.TicketError;
import java.util.Optional;
import org.apache.poi.ss.util.CellAddress;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author thiag
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketErrorTest {
    
    
    
    @Test
    public void hasError(){
        
        TicketError ticketError = new TicketError(Long.MIN_VALUE, Long.MIN_VALUE, "");
        
        ticketError.activeError("dataEmissao");
        
        assertTrue(ticketError.hasError());
        
        Optional<String> comment = ticketError.hasComments(new CellAddress(0, 1));
        
        assertTrue(comment.isPresent());
    }
    
    
}
