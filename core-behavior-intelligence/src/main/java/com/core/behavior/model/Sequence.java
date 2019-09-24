/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "sequence")
@Data
public class Sequence {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "table")
    @Enumerated(EnumType.STRING)
    private SequenceTableEnum table;

    @Column(name = "sequence")
    private Long sequenceMin;

    private transient Long sequenceMax;

    private transient Long sequenceCur;

    @PostLoad
    public void setValueMIn() {
        this.sequenceCur = this.sequenceMin;
    }

    public Long getSequence() {

        this.sequenceCur = ++this.sequenceCur;

        return this.sequenceCur;
    }

}
