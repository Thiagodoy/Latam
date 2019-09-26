/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */

@Data
@AllArgsConstructor
public class TicketValidationDTO {
 
    
    private Long update;
    private Long insert;
    private Long count;
    private Long cupom;
    private Long backoffice;
    
}
