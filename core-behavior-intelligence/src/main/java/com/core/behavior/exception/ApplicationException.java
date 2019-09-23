package com.core.behavior.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@AllArgsConstructor
@Data
public class ApplicationException extends RuntimeException {       
    private Long codeMessage;
}
