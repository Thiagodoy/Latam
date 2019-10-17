package com.core.behavior.exception;

import java.io.File;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */


@Data
public class ApplicationException extends RuntimeException {       
    private Long codeMessage;
    
    
    public ApplicationException(Long m){
        this.codeMessage = m;
    }
    
    public ApplicationException(Long m, String message){
        super(message);
        this.codeMessage = m;
    
    }
}
