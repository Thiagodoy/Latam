/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;

/**
 *
 * @author thiag
 */
public enum TicketLayoutEnum {
    SHORT,
    FULL;
    
    /**
     *
     * @param value
     * @return
     */
    public static TicketLayoutEnum getLayout(Long value){
        return value == 1 ? SHORT : FULL;
    }
}
