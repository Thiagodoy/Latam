/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;


/**
 *
 * @author thiag
 */
@Data
@Entity
@Table(schema = "behavior", name = "scorecard")
public class ScoreCard implements Serializable {

    @Id
    @Column(name = "id_scorecard")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_calendar", referencedColumnName = "id_calendar", nullable = false, insertable = true,updatable = true)
    private Calendar calendar;

    
    @OneToOne
    @JoinColumn( name= "id_agency", nullable = false, insertable = true,updatable = true)
    private Agency agency;

    @Column(name = "bda")
    private Long bda;

    @Column(name = "result")
    private String result;

    @Column(name = "adjusted_result")
    private String adjustedResult;

    @Column(name = "adjusted_user_id")
    private String adjustedUserId;

    @Column(name = "comments")
    private String comments;
    
    @Column(name = "adjusted_datetime")
    private LocalDateTime adjustedDatetime;
    
    @Column(name = "score_frequency")
    private Long scoreFrequency;
    
    @Column(name = "score_profile")
    private Long scoreProfile;
    
    @Column(name = "score_im")
    private Long scoreIm;
    
    @Column(name = "reviewed")
    private String reviewed;
    
    @Column(name = "approved")
    private String approved;
    
    @Column(name = "approved_user_id")
    private String approvedUserId;
    
    @Column(name = "approved_datetime") 
    private LocalDateTime approvedDatetime;
    
    @Column(name = "comments_analyst")
    private String commentsAnalyst;

    
     public ScoreCard(){
         this.reviewed = "N";
         this.approved = "N";
     }       
    

}
