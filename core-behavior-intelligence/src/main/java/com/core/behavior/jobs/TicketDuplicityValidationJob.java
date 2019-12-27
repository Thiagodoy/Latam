/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.dto.TicketValidationDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.TicketTypeEnum;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class TicketDuplicityValidationJob implements Runnable {

    private TicketService service;
    private Ticket ticket;

    public TicketDuplicityValidationJob(ApplicationContext context, Ticket ticket) {
        this.ticket = ticket;
        this.service = context.getBean(TicketService.class);
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
  

        } catch (Exception e) {
            Logger.getLogger(TicketDuplicityValidationJob.class.getName()).log(Level.SEVERE, MessageFormat.format("id -> {0}", ticket.getId()));
            Logger.getLogger(TicketDuplicityValidationJob.class.getName()).log(Level.SEVERE, "[ EXECUTOR ]", e);
            ticket.setStatus(TicketStatusEnum.ERROR_EXECUTOR);
           // service.save(ticket);
        }

    }

}
