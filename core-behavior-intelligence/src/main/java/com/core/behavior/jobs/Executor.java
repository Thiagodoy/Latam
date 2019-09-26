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
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            TicketValidationDTO rules = this.service.checkRules(ticket);

            if (rules.getInsert() == 0 || rules.getInsert() > 0 && rules.getUpdate() == 0) {
                ticket.setType(TicketTypeEnum.INSERT);
                //ticket.setStatus(TicketStatusEnum.APPROVED);

                if (rules.getInsert() > 0 && rules.getUpdate() == 0) {
                    List<Ticket> updates = this.service.findtFirstTicket(ticket);
                    Ticket uo = updates.parallelStream().min(Comparator.comparing(Ticket::getCupom)).get();
                    //FIXME Verificar se os demais tickets esta com o mesmo bilhete sempre manter o numero do primeiro
                    ticket.setBilheteBehavior(uo.getBilheteBehavior());
                }

            } else if (rules.getUpdate() > 0) {

                ticket.setType(TicketTypeEnum.UPDATE);
                //ticket.setStatus(TicketStatusEnum.APPROVED);
                List<Ticket> updates = this.service.findtToUpdate(ticket);
                Ticket uo = updates.parallelStream().min(Comparator.comparing(Ticket::getLineFile)).get();
                ticket.setBilheteBehavior(uo.getBilheteBehavior());

            } else if (rules.getBackoffice() > 0) {
                ticket.setStatus(TicketStatusEnum.BACKOFFICE);
                ticket.setBilheteBehavior(null);
            }

//            if (rules.getUpdate() > 0) {
//                ticket.setType(TicketTypeEnum.UPDATE);
//                ticket.setStatus(TicketStatusEnum.APPROVED);
//                List<Ticket> updates = this.service.findtToUpdate(ticket);
//                Ticket uo = updates.parallelStream().min(Comparator.comparing(Ticket::getLineFile)).get();
//                ticket.setBilheteBehavior(uo.getBilheteBehavior());
//            } else if (rules.getInsert() == 0) {
//                ticket.setType(TicketTypeEnum.INSERT);
//                ticket.setStatus(TicketStatusEnum.APPROVED);
//
//                if (!rules.getCupom().equals(rules.getCount())) {
//                    ticket.setStatus(TicketStatusEnum.BACKOFFICE_CUPOM);
//                } else if (rules.getCount() > 0L) {
//                    Ticket optT = service.findtFirstTicket(ticket).stream().sorted(Comparator.comparing(Ticket::getLineFile)).findFirst().get();
//
//                    if (optT != null && !ticket.getBilheteBehavior().equals(optT.getBilheteBehavior())) {
//                        ticket.setBilheteBehavior(optT.getBilheteBehavior());
//                    }
//                }
//            } else if (rules.getBackoffice() > 0) {
//                ticket.setStatus(TicketStatusEnum.BACKOFFICE);
//                ticket.setBilheteBehavior(null);
//            }
            service.save(ticket);

            rules = this.service.checkRules(ticket);

            //Validação de Cupom
//            if (!rules.getCupom().equals(rules.getCount())) {
//                ticket.setStatus(TicketStatusEnum.BACKOFFICE_CUPOM);
//                service.save(ticket);
//            }

        } catch (Exception e) {
            Logger.getLogger(Executor.class.getName()).log(Level.INFO, MessageFormat.format("id -> {0}", ticket.getId()));
            Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, "[ EXECUTOR ]", e);
            ticket.setStatus(TicketStatusEnum.ERROR_EXECUTOR);
            service.save(ticket);
        }

    }

}
