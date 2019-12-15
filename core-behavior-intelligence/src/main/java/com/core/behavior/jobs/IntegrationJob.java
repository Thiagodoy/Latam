package com.core.behavior.jobs;

import com.core.behavior.dto.FileLinesApprovedDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.services.FileService;
import com.core.behavior.services.IntegrationService;
import java.util.List;
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

    
    

    private Long fileId;

    public IntegrationJob(TicketService ticketService, FileService fileService, IntegrationService integrationService) {
        this.ticketService = ticketService;
        this.fileService = fileService;
        this.integrationService = integrationService; 
    }

    @Override
    public void run() {

        try {

            FileLinesApprovedDTO file = fileService.fileInfo(this.fileId);            
            this.getValues(file);

        } catch (Exception e) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, "[run] -> id = " + this.fileId, e);
        }

    }

    private void getValues(FileLinesApprovedDTO file) throws Exception {

        long start = System.currentTimeMillis();
        PageRequest page = PageRequest.of(0, file.getQtd().intValue());

        List<Ticket> tickets = ticketService.listByFileIdAndStatus(file.getFile(), TicketStatusEnum.APPROVED, page);       

        try {
            integrationService.integrate(tickets);
            ticketService.updateStatusAndFileIntegrationBatch(TicketStatusEnum.WRITED, tickets, "");
        } catch (Exception e) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, "[ integrate ] -> id = " + this.fileId, e);
        }       

        Logger.getLogger(IntegrationJob.class.getName()).log(Level.INFO, "[ IntegrationJob ] -> Tempo" + ((System.currentTimeMillis() - start) / 1000) + " sec");
    }      

}
