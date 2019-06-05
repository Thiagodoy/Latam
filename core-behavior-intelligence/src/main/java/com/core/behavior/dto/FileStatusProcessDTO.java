/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import java.math.BigInteger;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileStatusProcessDTO {
    
    
    private Long time;
    private Long qtd;
    private String status;
    
    public FileStatusProcessDTO(Date data, BigInteger qtd, String status){
        this.time = data.getTime();
        this.qtd = qtd.longValue();
        this.status = status;
    }
    
    
}
