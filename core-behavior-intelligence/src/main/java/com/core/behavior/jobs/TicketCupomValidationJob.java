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

            List<Ticket> tickets = service.findByAgrupamentoA(ticket);
            final int count = tickets.size();
            final int sumTicket = tickets.stream().map(t -> t.getCupom().intValue()).reduce(0, (a, b) -> a + b);

            final Double value = count * (((count - 1) * 0.5) + 1);

            if (value.intValue() == sumTicket) {
                ticket.setStatus(TicketStatusEnum.APPROVED);
            } else {
                ticket.setStatus(TicketStatusEnum.BACKOFFICE_CUPOM);
            }

            service.save(ticket);

        } catch (Exception e) {
            Logger.getLogger(TicketCupomValidationJob.class.getName()).log(Level.INFO, MessageFormat.format("id -> {0}", ticket.getId()));
            Logger.getLogger(TicketCupomValidationJob.class.getName()).log(Level.SEVERE, "[ TicketCupomValidationJob ]", e);
            ticket.setStatus(TicketStatusEnum.BACKOFFICE_CUPOM);
            service.save(ticket);
        }

    }

}
