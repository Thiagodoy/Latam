package com.core.behavior.services;

import com.core.behavior.model.Ticket;
import com.core.behavior.repository.TicketRepository;
import com.core.behavior.util.TicketStatusEnum;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import  static com.core.behavior.util.Utils.mountBatchInsert;
import  static com.core.behavior.util.Utils.TypeField;
import org.springframework.data.domain.PageRequest;


/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private FileService fileService;

    @Autowired
    private DataSource dataSource;

    public void saveBatch(List<Ticket> ticket) throws SQLException {
        
        long start = System.currentTimeMillis();
        long end;
        long fileId = ticket.get(0).getFileId();
        try {

            Connection con = dataSource.getConnection();
            List<String> inserts = new ArrayList<>();
            int count = 0;
            for (Ticket t : ticket) {
                inserts.add(mountBatchInsert(t,TypeField.TICKET));
                count++;
                if (count == 1000) {
                    String query = "INSERT INTO `behavior`.`ticket` VALUES " + inserts.stream().collect(Collectors.joining(","));
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.execute();                    
                    con.commit();
                    count = 0;
                    inserts.clear();
                }

            }

            String query = "INSERT INTO `behavior`.`ticket` VALUES " + inserts.stream().collect(Collectors.joining(","));
            PreparedStatement ps = con.prepareStatement(query);
            ps.execute();
            con.commit();

        } catch (SQLException ex) {
            Logger.getLogger(TicketService.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        
        end = System.currentTimeMillis();        
        fileService.setPersistTime(fileId, (end - start)/1000);
    }
    
    public List<Ticket>listByFileId(Long id){
        return ticketRepository.findByFileId(id);
    } 


    @Transactional()
    public List<Ticket> saveAll(List<Ticket> list) {
        return ticketRepository.saveAll(list);
    }

    @Transactional()
    public Ticket save(Ticket ti) {
        return ticketRepository.save(ti);
    }

    public long checkLong(Long value) {
        return value != null ? value : 0;
    }

    public double checkDouble(Double value) {
        return value != null ? value : 0.0;
    }

    public Date checDate(java.util.Date value) {
        return value != null ? new Date(value.getTime()) : null;
    }
    
    public List<Ticket> listDuplicityByFileId(Long fileId){
        return null;
    }
    
    public List<Ticket>listByStatus(TicketStatusEnum status,PageRequest page){        
        return ticketRepository.findByStatus(status,page);               
    }

}
