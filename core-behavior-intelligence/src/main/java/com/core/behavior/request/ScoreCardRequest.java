/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.request;

import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ScoreCardRequest {

    private Long id;

    private String result;

    private String adjustedResult;
    
    private String adjustedUserId;

    private String comments;    
    
    private String commentsAnalyst; 

    private String reviewed;

    private String approved;

    private String approvedUserId;

    

}
