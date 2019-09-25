/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import com.core.behavior.util.SequenceTableEnum;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "sequences")
@Data
@DynamicUpdate
public class Sequence {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "table")
    @Enumerated(EnumType.STRING)
    private SequenceTableEnum table;

    @Column(name = "sequence")
    private Long sequenceMin;
    
    @Column(name = "vs")
    @Version
    private Long version;

    private transient Long sequenceMax;
    private transient Long sequenceCur;

    @PostLoad
    public void setValueMIn() {
        this.sequenceCur = this.sequenceMin;
    }

    public synchronized Long getSequence() {

        this.sequenceCur = ++this.sequenceCur;

        return this.sequenceCur;
    }

}
