/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.dto.FileLinesApprovedDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.services.FileService;
import com.core.behavior.services.IntegrationService;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author thiag
 */
@Data
public class IntegrationJob implements Runnable {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private FileService fileService;

    @Autowired
    private IntegrationService integrationService;

    private Map<Long, IntegrationJob> pool;

    private Long fileId;

    public IntegrationJob(TicketService ticketService, FileService fileService, IntegrationService integrationService) {
        this.ticketService = ticketService;
        this.fileService = fileService;
        this.integrationService = integrationService;

    }

    @Override
    public void run() {

        try {

            Logger.getLogger(IntegrationJob.class.getName()).log(Level.INFO, "Inciando integração arquivo ->" + fileId);

            FileLinesApprovedDTO file = fileService.fileInfo(this.fileId);
            this.getValues(file);
            this.removeFromPoll();
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.INFO, "Finalizado integração arquivo ->" + fileId);

        } catch (Exception e) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, "[run] -> id = " + this.fileId, e);
        }

    }

    
    private void removeFromPoll(){
        synchronized(this.pool){
            this.pool.remove(this.fileId);
        }
    }
    
    private void getValues(FileLinesApprovedDTO file) throws Exception {

        long start = System.currentTimeMillis();
        PageRequest page = PageRequest.of(0, file.getQtd().intValue());
        
        
        //Sujeito arealizar a parallelização

        List<Ticket> tickets = ticketService.listByFileIdAndStatus(file.getFile(), TicketStatusEnum.APPROVED, page);

        try {
            integrationService.integrate(tickets);
            ticketService.updateStatusAndFileIntegrationBatch(TicketStatusEnum.WRITED, tickets, "");
        } catch (Exception e) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, "[ integrate ] -> id = " + this.fileId, e);
        } finally {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.INFO, "[ getValues ] -> Tempo" + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }

    }

}
