/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.model.Ticket;
import com.core.behavior.util.TicketStatusEnum;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiag
 */
public class TicketCupomValidationJob1 implements Runnable {

    private List<Ticket> tickets;

    public TicketCupomValidationJob1(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public void run() {

        try {
            //sum(cupom) = truncate(count(1)*(((count(1)-1) * 0.5)+1),0)

            Long sumCupom = this.tickets.stream().map((t) -> t.getCupom()).reduce(0L, Long::sum);
            Long count = this.tickets.stream().count();

            Long result = (long) (count * (((count - 1) * 0.5) + 1));

            if (result.equals(sumCupom)) {
                this.tickets.stream().forEach(t -> t.setStatus(TicketStatusEnum.APPROVED));
            } else {
                this.tickets.stream().forEach(t -> t.setStatus(TicketStatusEnum.BACKOFFICE_CUPOM));
            }

        } catch (Exception e) {
            Logger.getLogger(TicketCupomValidationJob1.class.getName()).log(Level.SEVERE, "[ run ]", e);
        }

    }

}
