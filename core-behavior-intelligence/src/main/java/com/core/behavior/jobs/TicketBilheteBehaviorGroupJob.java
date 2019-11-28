/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketTypeEnum;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class TicketBilheteBehaviorGroupJob implements Runnable {

    private TicketService service;
    private Ticket ticket;

    public TicketBilheteBehaviorGroupJob(ApplicationContext context, Ticket ticket) {
        this.ticket = ticket;
        this.service = context.getBean(TicketService.class);
    }

    @Override
    public void run() {

        if (this.ticket.getLayout().equals(TicketLayoutEnum.FULL)) {

            List<Ticket> tickets = this.service.findByAgrupamentoA(ticket);
//                    .stream()
//                    .filter(t -> !t.type.equals(TicketTypeEnum.UPDATE))
//                    .collect(Collectors.toList());

            Optional<Ticket> uo = tickets.parallelStream().min(Comparator.comparing(Ticket::getCupom));

            if (uo.isPresent() && tickets.size() > 1) {
                tickets.stream()
                        .filter(r -> !r.getId().equals(uo.get().getId()))
                        .forEach(t -> {
                            t.setBilheteBehavior(uo.get().getBilheteBehavior());
                            this.service.save(t);
                        });

            }

        } else {
            // TODO implmentar para o Layout 20 Colunas
        }

    }

}
