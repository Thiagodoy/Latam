/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import com.core.behavior.annotations.PositionParameter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@Entity
@Table(name = "ticket_key")
public class TicketKey { 
    
    
    @Id
    @Column(name = "id")
    @PositionParameter(value = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @PositionParameter(value = 1)
    @Column(name = "key")
    public String key; 
    
    public TicketKey(String key){
        //this.id = 0L;
        this.key = key;
    }
    
}
