/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.dto.TicketCountCupomDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.TicketStatusEnum;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiag
 */
public class TicketCupomValidationJob implements Runnable {

    private TicketService service;

    private Ticket ticket;

    public TicketCupomValidationJob(TicketService service, Ticket ticket) {
        this.ticket = ticket;
        this.service = service;
    }

    @Override
    public void run() {

        try {

            //Validação para ticket de 49 colunas
            boolean isOk = service.checkCupom(ticket);
            TicketStatusEnum status = isOk ? TicketStatusEnum.APPROVED : TicketStatusEnum.BACKOFFICE_CUPOM;
            ticket.setStatus(status);
            service.save(ticket);

        } catch (Exception e) {
            Logger.getLogger(TicketCupomValidationJob.class.getName()).log(Level.SEVERE, MessageFormat.format("id -> {0}", ticket.getId()));
            Logger.getLogger(TicketCupomValidationJob.class.getName()).log(Level.SEVERE, "[ TicketCupomValidationJob ]", e);
            ticket.setStatus(TicketStatusEnum.BACKOFFICE_CUPOM);
            service.save(ticket);
        }

    }

}
