/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;

/**
 *
 * @author thiag
 */
public class TicketCupomValidationJob implements Runnable{
    
    
    private TicketService service;

    private Ticket ticket;
    
    public TicketCupomValidationJob(TicketService service, Ticket ticket) {
        this.ticket = ticket;
        this.service = service;
    }

    @Override
    public void run() {
        
        
        
        
        
        
        
        
    }
    
}
