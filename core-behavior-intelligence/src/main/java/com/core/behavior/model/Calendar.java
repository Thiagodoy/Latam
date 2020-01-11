/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;



import javax.persistence.Table;


/**
 *
 * @author thiag
 */

@Data
@Entity
@Table(schema = "behavior", name = "calendar")
public class Calendar implements Serializable{
    
    @Id   
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id_calendar")
    private Long id;
    
    @Column(name = "period")
    private String period; 
    
    @Column(name = "work_days")
    private Long WorkDays;    
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR")  
    @Column(name = "date_initial")
    private LocalDate dateInit;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR")  
    @Column(name = "date_final")
    private LocalDate dateEnd;
    
    
}
