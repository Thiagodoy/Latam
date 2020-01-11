/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
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
@Table(schema = "behavior", name = "holiday")
public class Holiday {

    @Id
    @Column(name = "id_holiday")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "holiday_description")
    private String description;

    @Column(name = "day")
    private Integer day;
    
    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR")   
    @Column(name = "holiday_date",  columnDefinition = "DATE")
    private LocalDate date;
}
