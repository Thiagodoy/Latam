/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.dto.TicketValidationDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.model.TicketStage;
import com.core.behavior.util.Utils;
import static com.core.behavior.util.Utils.mountBatchInsert;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class TicketStageService {
    
    
    @Autowired
    private DataSource dataSource;
    
     
    
     public void saveBatch(List<TicketStage> ticket) throws SQLException {

        if (ticket.isEmpty()) {
            return;
        }
              
        try {

            Connection con = dataSource.getConnection();
            List<String> inserts = new ArrayList<>();
            int count = 0;
            for (TicketStage t : ticket) {
                inserts.add(mountBatchInsert(t, Utils.TypeField.TICKET_STAGE));
                count++;
                if (count == 3000) {
                    String query = "INSERT INTO `behavior`.`ticket_stage` VALUES " + inserts.stream().collect(Collectors.joining(","));
                    Statement ps = con.createStatement();
                    ps.clearBatch();

                    ps.addBatch(query);
                    ps.executeBatch();
                    con.commit();
                    count = 0;
                    inserts.clear();
                }

            }

            String query = "INSERT INTO `behavior`.`ticket_stage` VALUES " + inserts.stream().collect(Collectors.joining(","));
            Statement ps = con.createStatement();
            ps.clearBatch();

            ps.addBatch(query);
            ps.executeBatch();            
            con.commit();

        } catch (SQLException ex) {
            Logger.getLogger(TicketKeyService.class.getName()).log(Level.SEVERE, "[ saveBatch ]", ex);
            throw ex;
        }

        
        
    }
    
    
    
    
}
