/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import java.util.List;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class FileIntegrationDTO {
    
    private HeaderShortIntegrationDTO header = new HeaderShortIntegrationDTO();
    private HeaderFullIntegrationDTO headerFull = new HeaderFullIntegrationDTO();
    private List<TicketIntegrationDTO> integrationDTOs;
}
