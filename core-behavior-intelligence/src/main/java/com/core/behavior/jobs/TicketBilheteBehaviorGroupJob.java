/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.model.Ticket;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiag
 */
public class TicketBilheteBehaviorGroupJob implements Runnable {

    private List<Ticket> tickets;

    public TicketBilheteBehaviorGroupJob(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public void run() {

        try {

            Optional<Ticket> opt = this.tickets.stream().filter((t) -> t.getCupom().equals(1L)).findFirst();

            if (opt.isPresent()) {
                String bilheteBehavior = opt.get().getBilheteBehavior();
                this.tickets.stream().forEach((t) -> t.setBilheteBehavior(bilheteBehavior));
            }

        } catch (Exception e) {
            Logger.getLogger(TicketBilheteBehaviorGroupJob.class.getName()).log(Level.SEVERE, "[ run ]", e);
        }

    }

}
