/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.comparator;

import com.core.behavior.model.Ticket;
import java.util.Comparator;

/**
 *
 * @author thiag
 */
public class TicketComparator implements Comparator<Ticket> {

    @Override
    public int compare(Ticket a, Ticket b) {
        String bilheteA = a.getBilheteBehavior();
        String bilheteB = b.getBilheteBehavior();

        int c = bilheteA.compareTo(bilheteB);

        if (c != 0) {
            return c;
        }

        Long cupomA = a.getCupom();
        Long cupomB = b.getCupom();
        int d = cupomA.compareTo(cupomB);
        return d;
    }

}
