package com.core.behavior.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@AllArgsConstructor
@Data
public class ActivitiException extends Exception {
    private HttpStatus code;
    private String messageError;
    private Long codeMessage;
}
