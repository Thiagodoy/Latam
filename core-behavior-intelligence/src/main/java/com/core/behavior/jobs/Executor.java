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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */

public class Executor implements Runnable {

    
    private TicketService service;

    private Ticket ticket;

    public Executor(TicketService service, Ticket ticket) {
        this.ticket = ticket;
        this.service = service;
    }

    @Override
    public void run() {

        try {

            List<TicketValidationDTO> rules = this.service.checkRules(ticket);

            Optional<TicketValidationDTO> update = rules.stream().filter(r -> r.getRule().equals("UPDATE")).findFirst();
            Optional<TicketValidationDTO> insert = rules.stream().filter(r -> r.getRule().equals("INSERT")).findFirst();
            Optional<TicketValidationDTO> backoffice = rules.stream().filter(r -> r.getRule().equals("BACKOFFICE")).findFirst();
            Optional<TicketValidationDTO> count = rules.stream().filter(r -> r.getRule().equals("COUNT")).findFirst();
            Optional<TicketValidationDTO> cupom = rules.stream().filter(r -> r.getRule().equals("CUPOM")).findFirst();

            if (update.get().getValue() > 0) {
                ticket.setType(TicketTypeEnum.UPDATE);
                ticket.setStatus(TicketStatusEnum.APPROVED);
                List<Ticket> updates = this.service.findtToUpdate(ticket);
                Ticket uo = updates.parallelStream().min(Comparator.comparing(Ticket::getCupom)).get();
                ticket.setBilheteBehavior(uo.getBilheteBehavior());
            } else if (insert.get().getValue() == 0) {
                ticket.setType(TicketTypeEnum.INSERT);
                ticket.setStatus(TicketStatusEnum.APPROVED);

                if (cupom.isPresent() && !cupom.get().getValue().equals(count.get().getValue())) {
                    ticket.setStatus(TicketStatusEnum.BACKOFFICE_CUPOM);
                } else if (count.get().getValue() > 0L) {
                    Ticket optT = service.findtFirstTicket(ticket);

                    if (optT != null && !ticket.getBilheteBehavior().equals(optT.getBilheteBehavior())) {
                        ticket.setBilheteBehavior(optT.getBilheteBehavior());
                    }
                }
            } else if (backoffice.get().getValue() > 0) {
                ticket.setStatus(TicketStatusEnum.BACKOFFICE);
                ticket.setBilheteBehavior(null);
            }

            service.save(ticket);

        } catch (Exception e) {
             Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, "[ EXECUTOR ]", e);
            ticket.setStatus(TicketStatusEnum.ERROR_EXECUTOR);
            service.save(ticket);
        }

    }

}
