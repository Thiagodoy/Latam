package com.core.behavior.services;

import com.core.behavior.dto.TicketCountCupomDTO;
import com.core.behavior.dto.TicketDuplicityDTO;
import com.core.behavior.dto.TicketValidationDTO;
import com.core.behavior.dto.TicketValidationShortDTO;
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
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
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
    
    
    public void saveOnly(Ticket ticke) throws SQLException{
        this.saveBatch(Arrays.asList(ticke));
    }

    public void saveBatch(List<Ticket> ticket) throws SQLException {
        
        
        if(ticket.isEmpty()){
            return;
        }
        
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
                if (count == 3000) {
                    String query = "INSERT INTO `behavior`.`ticket` VALUES " + inserts.stream().collect(Collectors.joining(","));
                    Statement ps = con.createStatement();
                    ps.clearBatch();
                    ps.addBatch(query);
                    int[]ids = ps.executeBatch();
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
    
    public List<TicketDuplicityDTO> listDuplicityByDateEmission(LocalDate start, LocalDate end){
        return null;//ticketRepository.listDuplicityByDateEmission(start, end);
    }
    
    public TicketValidationDTO checkRules(Ticket ticket){
        return ticketRepository.rules(ticket.getAgrupamentoA(), ticket.getAgrupamentoB(), ticket.getCupom());
    }
    
    public TicketValidationShortDTO rulesShort(Ticket ticket){
        return ticketRepository.rulesShort(ticket.getAgrupamentoC(), ticket.getCupom());
    }
    
    public List<Ticket> findToUpdate(Ticket ticket){
        return ticketRepository.findToUpdate(ticket.getAgrupamentoA(),ticket.getCupom());
    }
    
    public List<Ticket> listByDateEmission(java.util.Date start, java.util.Date end, String codigoAgencia){
        return ticketRepository.findBydataEmissaoBetween(start, end);
    }
    
    public TicketCountCupomDTO rulesCountCupom(Ticket ticket){
        return ticketRepository.rulesCountCupom(ticket.getAgrupamentoA(), ticket.getCupom());
    }
    
    public TicketCountCupomDTO rulesCountCupomShort(Ticket ticket){
        return ticketRepository.rulesCountCupomShort(ticket.getAgrupamentoC(), ticket.getCupom());
    }
    
    
    public List<Ticket> findByAgrupamentoC(Ticket t){
        return ticketRepository.findByAgrupamentoC(t.getAgrupamentoC());
    }
    
    public List<Ticket> findByAgrupamentoA(Ticket t){
        return ticketRepository.findByAgrupamentoA(t.getAgrupamentoA());
    }

}
