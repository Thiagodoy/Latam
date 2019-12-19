/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.model.Ticket;
import com.core.behavior.model.TicketStage;
import com.core.behavior.services.IntegrationService;
import com.core.behavior.services.TicketStageService;
import com.core.behavior.util.TicketLayoutEnum;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class TicketBilheteBehaviorGroupJob implements Runnable {

    private TicketStageService service;
    private Ticket ticket;

    public TicketBilheteBehaviorGroupJob(ApplicationContext context, Ticket ticket) {
        this.ticket = ticket;
        this.service = context.getBean(TicketStageService.class);
    }

    @Override
    public void run() {

        try {
            Optional<TicketStage> ticketStage = this.service.getByAgrupamentoAAndCupom(ticket);
            
            if(ticketStage.isPresent()){
                ticket.setBilheteBehavior(ticketStage.get().getBilhetBehavior());    
            }           
            
        } catch (Exception e) {            
             Logger.getLogger(TicketBilheteBehaviorGroupJob.class.getName()).log(Level.SEVERE, "[ run ]", e);            
        }       

    }

}
