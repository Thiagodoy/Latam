/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Tag;
import com.core.behavior.aws.client.ClientIntegrationAws;
import com.core.behavior.dto.FileIntegrationDTO;
import com.core.behavior.dto.FileLinesApprovedDTO;
import com.core.behavior.dto.TicketIntegrationDTO;
import com.core.behavior.model.FileIntegration;
import com.core.behavior.model.Ticket;
import com.core.behavior.repository.FileIntegrationRepository;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.Stream;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.io.BeanIoWriter;
import com.core.behavior.services.FileService;
import com.core.behavior.services.IntegrationService;
import com.core.behavior.util.TicketTypeEnum;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
