/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;


/**
 *
 * @author thiag
 */
@Data
@Entity
@Table(schema = "behavior", name = "note_im")
public class NoteIm {
    
    @Id
    @Column(name = "id_note_im")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name ="id_agency", nullable = false, insertable = true,updatable = true)
    private Agency agency;
    
    @OneToOne
    @JoinColumn(name = "id_calendar", insertable = true,updatable = true, nullable = false)
    private Calendar calendar;
    
    @Column(name = "delivered")
    private String delivered;
    
    @Column(name = "id_user")
    private String user;
    
    @Column(name = "datetime")
    private LocalDateTime dateTime;
    
    @PrePersist
    public void valueDefault(){
        this.delivered = "N";
    }
    
}
