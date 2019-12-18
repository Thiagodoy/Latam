/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.dto.TicketValidationDTO;
import com.core.behavior.dto.TicketValidationShortDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.TicketTypeEnum;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiag
 */
public class TicketDuplicityValidationJob implements Runnable {

    private TicketService service;
    private Ticket ticket;

    public TicketDuplicityValidationJob(TicketService service, Ticket ticket) {
        this.ticket = ticket;
        this.service = service;
    }

    @Override
    public void run() {

        try {
            
            
            
            
            TicketValidationDTO rules = this.service.checkRules(ticket);
            
             if (rules.getDuplicity() > 1) {
                ticket.setStatus(TicketStatusEnum.BACKOFFICE_DUPLICITY);
                ticket.setBilheteBehavior(null);
            } else{
                ticket.setType(TicketTypeEnum.INSERT);
            }
            
            
//            
//
//            switch (this.ticket.getLayout()) {
//                case FULL:
//
//                    TicketValidationDTO rules = this.service.checkRules(ticket);
//
//                    if(rules.getDuplicity() > 0){
//                        ticket.setStatus(TicketStatusEnum.BACKOFFICE_DUPLICITY);
//                        ticket.setBilheteBehavior(null);
//                    }else if ((rules.getInsert() == 0 || rules.getInsert() > 0) && rules.getUpdate() == 0) {
//                        ticket.setType(TicketTypeEnum.INSERT);
//                    } else if (rules.getBackoffice() > 0) {
//                        ticket.setStatus(TicketStatusEnum.BACKOFFICE);
//                        ticket.setBilheteBehavior(null);
//                    }
//
//                    break;
//                case SHORT:                   
//                    TicketValidationShortDTO rulesShort = this.service.rulesShort(ticket);
//                    
//                    if(rulesShort.getInsert() == 0){
//                        ticket.setType(TicketTypeEnum.INSERT);
//                    }else if(rulesShort.getUpdate() > 0){
//                        ticket.setType(TicketTypeEnum.UPDATE);      
//                        List<Ticket> listUpdates = service.findByAgrupamentoC(ticket);
//                        Ticket tOld = listUpdates.parallelStream().sorted(Comparator.comparing(Ticket::getCreatedAt)).findFirst().get();                        
//                        ticket.setBilheteBehavior(tOld.getBilheteBehavior());
//                    }
//                    break;
//            }

           // service.save(ticket);

        } catch (Exception e) {
            Logger.getLogger(TicketDuplicityValidationJob.class.getName()).log(Level.SEVERE, MessageFormat.format("id -> {0}", ticket.getId()));
            Logger.getLogger(TicketDuplicityValidationJob.class.getName()).log(Level.SEVERE, "[ EXECUTOR ]", e);
            ticket.setStatus(TicketStatusEnum.ERROR_EXECUTOR);
           // service.save(ticket);
        }

    }

}
