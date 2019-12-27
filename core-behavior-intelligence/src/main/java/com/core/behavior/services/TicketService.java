package com.core.behavior.services;

import com.core.behavior.dto.TicketCountCupomDTO;
import com.core.behavior.dto.TicketDuplicityDTO;
import com.core.behavior.dto.TicketValidationDTO;
import com.core.behavior.dto.TicketValidationShortDTO;
import com.core.behavior.jobs.IntegrationJob;
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

import static com.core.behavior.util.Utils.mountBatchInsert;
import static com.core.behavior.util.Utils.TypeField;
import java.sql.Statement;
import java.text.MessageFormat;
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

    public void saveOnly(Ticket ticke) throws SQLException {
        this.saveBatch(Arrays.asList(ticke));
    }

    @Transactional
    public void saveBatch(List<Ticket> ticket) throws SQLException {

        if (ticket.isEmpty()) {
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
                inserts.add(mountBatchInsert(t, TypeField.TICKET));
                count++;
                if (count == 3000) {
                    String query = "INSERT INTO `behavior`.`ticket` VALUES " + inserts.stream().collect(Collectors.joining(","));
                    Statement ps = con.createStatement();
                    ps.clearBatch();
                    ps.addBatch(query);
                    ps.executeBatch();
                    con.commit();
                    count = 0;
                    inserts.clear();
                }

            }

            String query = "INSERT INTO `behavior`.`ticket` VALUES " + inserts.stream().collect(Collectors.joining(","));
            Statement ps = con.createStatement();
            ps.clearBatch();
            ps.addBatch(query);
            ps.executeBatch();
            con.commit();

        } catch (SQLException ex) {
            Logger.getLogger(TicketService.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

        end = System.currentTimeMillis();
        fileService.setPersistTime(fileId, (end - start) / 1000);
    }

    public void updateStatusAndFileIntegrationBatch(TicketStatusEnum status, List<Ticket> tickets, String filename) throws SQLException {

        if (tickets.isEmpty()) {
            return;
        }

        filename = "'" + filename + "'";
        String sstatus = "'" + status.toString() + "'";
        Long id = tickets.get(0).getFileId();

        Logger.getLogger(TicketService.class.getName()).log(Level.INFO, "[ updateStatusAndFileIntegrationBatch ] status -> " + status + ", ticket size -> " + tickets.size() + " fileId = " + id);

        long start = System.currentTimeMillis();
        long end;
        
        boolean log = true;

        try {

            Connection con = dataSource.getConnection();
            List<Long> update = new ArrayList<>();
            int count = 0;
            while (!tickets.isEmpty()) {
                update.add(tickets.remove(0).getId());
                count++;
                if (count == 1000) {
                    String ids = update.stream().parallel().map((i) -> {
                        return i.toString();
                    }).collect(Collectors.joining(","));

                    String query = MessageFormat.format("UPDATE `behavior`.`ticket` SET status = {0}, file_integration = {1} WHERE id IN({2})", sstatus, filename, ids);
                    Statement ps = con.createStatement();
                    ps.clearBatch();

                    ps.addBatch(query);

                    ps.executeLargeBatch();
                    con.commit();
                    count = 0;
                    update.clear();

                }

            }

            String ids = update.stream().parallel().map((i) -> {
                return i.toString(); //To change body of generated lambdas, choose Tools | Templates.
            }).collect(Collectors.joining(","));

            String query = MessageFormat.format("UPDATE `behavior`.`ticket` SET status = {0}, file_integration = {1} WHERE id IN({2})", sstatus, filename, ids);

            Statement ps = con.createStatement();
            ps.clearBatch();
            ps.addBatch(query);
            ps.executeLargeBatch();
            con.commit();
            update.clear();

        } catch (Exception ex) {
            Logger.getLogger(TicketService.class.getName()).log(Level.SEVERE, "[ updateStatusAndFileIntegrationBatch ]", ex);
            throw ex;
        }finally{
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.INFO, "[ updateStatusAndFileIntegrationBatch ] -> Tempo" + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }
        

    }

    public List<Ticket> listByFileId(Long id) {
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

    public List<Ticket> listDuplicityByFileId(Long fileId) {
        return null;
    }

    public List<Ticket> listByStatus(TicketStatusEnum status, PageRequest page) {
        return ticketRepository.findByStatus(status, page);
    }

    public List<Ticket> listByFileIdAndStatus(Long fileId, TicketStatusEnum status, PageRequest page) {
        return ticketRepository.findByFileIdAndStatus(fileId, status, page);
    }

    public List<Ticket> listByFileIdAndStatus(Long fileId, TicketStatusEnum status) {
        return ticketRepository.findByFileIdAndStatus(fileId, status);
    }

    public List<TicketDuplicityDTO> listDuplicityByDateEmission(LocalDate start, LocalDate end) {
        return null;//ticketRepository.listDuplicityByDateEmission(start, end);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public TicketValidationDTO checkRules(Ticket ticket) {
        return ticketRepository.rules(ticket.getAgrupamentoA(), ticket.getCupom());
    }

    public TicketValidationShortDTO rulesShort(Ticket ticket) {
        return ticketRepository.rulesShort(ticket.getAgrupamentoC(), ticket.getCupom());
    }

    public List<Ticket> findToUpdate(Ticket ticket) {
        return ticketRepository.findToUpdate(ticket.getAgrupamentoA(), ticket.getCupom());
    }

    public List<Ticket> listByDateEmission(java.util.Date start, java.util.Date end, String codigoAgencia) {
        return ticketRepository.findBydataEmissaoBetween(start, end);
    }

    public TicketCountCupomDTO rulesCountCupom(Ticket ticket) {
        return ticketRepository.rulesCountCupom(ticket.getAgrupamentoA(), ticket.getCupom());
    }

    public TicketCountCupomDTO rulesCountCupomShort(Ticket ticket) {
        return ticketRepository.rulesCountCupomShort(ticket.getAgrupamentoC(), ticket.getCupom());
    }

    public List<Ticket> findByAgrupamentoC(Ticket t) {
        return ticketRepository.findByAgrupamentoC(t.getAgrupamentoC());
    }

    public List<Ticket> findByAgrupamentoA(Ticket t) {
        return ticketRepository.findByAgrupamentoA(t.getAgrupamentoA());
    }
    
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public boolean checkCupom(Ticket ticket){        
        return this.ticketRepository.checkCupom(ticket.getAgrupamentoA()).longValue() == 1L;
    }
    
    @Transactional
    public void deleteByDateBetween(LocalDate start, LocalDate end){
        this.ticketRepository.deleteByDateBetween(start, end);
    }

}
