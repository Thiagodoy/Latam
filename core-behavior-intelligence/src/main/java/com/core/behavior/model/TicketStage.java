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
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Data
@AllArgsConstructor
@Table(name = "ticket_stage")
public class TicketStage {

    @Id
    @Column(name = "id")
    @PositionParameter(value = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @PositionParameter(value = 1)
    @Column(name = "cupom")
    public Long cupom;

    @PositionParameter(value = 2)
    @Column(name = "agrupamento_a")
    public String agrupamentoA;

    @PositionParameter(value = 3)
    @Column(name = "agrupamento_b")
    public String agrupamentoB;
    
    @PositionParameter(value = 4)
    @Column(name = "bilhete_behavior")
    public String bilhetBehavior;

}
