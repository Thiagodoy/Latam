/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import com.core.behavior.util.LayoutEmailEnum;
import com.core.behavior.util.NotificacaoStatusEnum;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "notificacao")
@Data
public class Notificacao {
    
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "LAYOUT")    
    private LayoutEmailEnum layout;
    
    @Column(name = "parameters")
    private String parameters;   
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificacaoStatusEnum status;
    
    @Column(name = "error")
    private String erro;
    
    @Column(name = "retry")
    private Long retry;
    
    @Column(name = "create_at")
    private LocalDateTime createAt;
    
    
    @PrePersist
    public void create(){
        this.retry = 0l;
        this.status = NotificacaoStatusEnum.READY;
        this.createAt = LocalDateTime.now();
    }   
    
}
