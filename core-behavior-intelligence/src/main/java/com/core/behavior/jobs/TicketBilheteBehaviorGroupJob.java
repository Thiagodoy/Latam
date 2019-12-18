/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.model.Ticket;
import com.core.behavior.model.TicketStage;
import com.core.behavior.services.TicketStageService;
import com.core.behavior.util.TicketLayoutEnum;
import java.util.Comparator;
import java.util.Optional;
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

        if (this.ticket.getLayout().equals(TicketLayoutEnum.FULL)) {
            TicketStage ticketStage = this.service.getByAgrupamentoAAndCupom(ticket.getAgrupamentoA());
            ticket.setBilheteBehavior(ticketStage.getBilhetBehavior());
        } else {
            // TODO implmentar para o Layout 20 Colunas
        }

    }

}
