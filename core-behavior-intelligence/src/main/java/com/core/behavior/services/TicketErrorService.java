/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.model.Log;
import com.core.behavior.model.TicketError;
import com.core.behavior.repository.TicketErrorRepository;
import com.core.behavior.util.Utils;
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
public class TicketErrorService {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private TicketErrorRepository repository;
    
    
    public List<TicketError>findByFileId(Long id){
        return repository.findByFileId(id);
    }
    
    public void saveBatch(List<TicketError> logs) {

        try {
            Connection con = dataSource.getConnection();
            List<String> inserts = new ArrayList<>();
            int count = 0;
            for (TicketError t : logs) {
                inserts.add(Utils.<Log>mountBatchInsert(t, Utils.TypeField.TICKET_ERROR));
                count++;
                if (count == 3000) {
                    String query = "INSERT INTO `behavior`.`ticket_error` VALUES " + inserts.stream().collect(Collectors.joining(","));
                    Statement ps = con.createStatement();
                    ps.clearBatch();
                    ps.addBatch(query);
                    ps.executeBatch();
                    con.commit();
                    count = 0;
                    inserts.clear();

                }
            }

            if (inserts.size() > 0) {

                String query = "INSERT INTO `behavior`.`ticket_error` VALUES " + inserts.stream().collect(Collectors.joining(","));
                Statement ps = con.createStatement();
                ps.clearBatch();
                ps.addBatch(query);
                ps.executeBatch();
                con.commit();
                count = 0;
                inserts.clear();

            }

        } catch (SQLException ex) {
            Logger.getLogger(TicketErrorService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    
}
