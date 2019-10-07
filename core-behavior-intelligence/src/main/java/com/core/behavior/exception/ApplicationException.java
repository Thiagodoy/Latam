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
    private File fileHeaderReturn;
    
    public ApplicationException(Long m){
        this.codeMessage = m;
    }
    
    public ApplicationException(Long m, File file){
        this.codeMessage = m;
        this.fileHeaderReturn = file;
    }
}
