package com.core.behavior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data

public class FileStatusDTO {

    private String fieldName;

    private Long qtdErrors;

    private Double percentualError;
    
    private Double percentualHit;
    
    private Long qtdTotalLines;

    public FileStatusDTO(String fieldName, Long qtdErrors, Double percentualError, Double percentualHit, Long qtdTotalLines) {
        this.fieldName = fieldName;
        this.qtdErrors = qtdErrors;
        this.percentualError = percentualError;
        this.percentualHit = percentualHit;
        this.qtdTotalLines = qtdTotalLines;
    }
    
    

}
