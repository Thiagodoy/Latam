/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.TicketTypeEnum;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class TicketDuplicityValidationJob1 implements Runnable {
    
    private List<Ticket> tickets;

    public TicketDuplicityValidationJob1(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public void run() {

        try {

            Map<String, Long> result = tickets.stream().collect(Collectors.groupingBy(t -> t.agrupamentoA + "-" + t.cupom, Collectors.counting()));

            result.keySet().stream().forEach(key -> {

                if (result.get(key) > 1) {

                    String[] values = key.split("-");
                    Long cupom = Long.parseLong(values[1]);
                    String agrupamento = values[0];

                    tickets
                            .stream()
                            .filter((t) -> t.getCupom().equals(cupom) && t.getAgrupamentoA().equals(t.getAgrupamentoA()))
                            .forEach((t) -> t.setStatus(TicketStatusEnum.BACKOFFICE_DUPLICITY));

                }
            });

            this.tickets
                    .parallelStream()
                    .filter(t -> !t.getStatus().equals(TicketStatusEnum.BACKOFFICE_DUPLICITY))
                    .forEach((t) -> t.setType(TicketTypeEnum.INSERT));

        } catch (Exception e) {
            Logger.getLogger(TicketDuplicityValidationJob1.class.getName()).log(Level.SEVERE, "[ EXECUTOR ]", e);
        }

    }

}
