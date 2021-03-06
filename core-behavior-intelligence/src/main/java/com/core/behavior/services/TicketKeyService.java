/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.model.Ticket;
import com.core.behavior.model.TicketKey;
import com.core.behavior.util.Utils;
import static com.core.behavior.util.Utils.mountBatchInsert;
import java.sql.CallableStatement;
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
public class TicketKeyService {

    @Autowired
    private DataSource dataSource;
    private Connection connection;

     public void saveBatch(List<TicketKey> ticket) throws SQLException {

        if (ticket.isEmpty()) {
            return;
        }
              
        try {

            Connection con = dataSource.getConnection();
            List<String> inserts = new ArrayList<>();
            int count = 0;
            for (TicketKey t : ticket) {
                inserts.add(mountBatchInsert(t, Utils.TypeField.TICKET_KEY));
                count++;
                if (count == 3000) {
                    String query = "INSERT INTO `behavior`.`ticket_key` VALUES " + inserts.stream().collect(Collectors.joining(","));
                    Statement ps = con.createStatement();
                    ps.clearBatch();

                    ps.addBatch(query);
                    ps.executeBatch();
                    con.commit();
                    count = 0;
                    inserts.clear();
                }

            }

            String query = "INSERT INTO `behavior`.`ticket_key` VALUES " + inserts.stream().collect(Collectors.joining(","));
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
    
    public void callProcDeleteTickets(){
        
          try {
            this.connection = dataSource.getConnection();
            CallableStatement c = this.connection.prepareCall("{call behavior.deleteTickets}");
            c.execute();
            this.connection.commit();
        } catch (Exception e) {
            Logger.getLogger(IntegrationService.class.getName()).log(Level.SEVERE, "[ callProcDeleteTickets ]", e);
        }        
        
    }

}
