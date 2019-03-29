package com.core.behavior.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@AllArgsConstructor
public class Response <T> {

    private T result;
    private Long codeMessage;    
    
    public static <T> Response build(T result, Long codeMEssage ){
        return new Response(result, codeMEssage);
    }
}
