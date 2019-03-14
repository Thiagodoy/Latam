package com.core.behavior.util;

import org.springframework.stereotype.Component;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Component
public class ActivitiConstantePath {  
    
    
    public static final String PATH_POST_USER = "/identity/users";
    public static final String PATH_GET_USER = "/identity/users";
    public static final String PATH_GET_SINGLE_USER = "/identity/users/{id}";
    public static final String PATH_DELETE_USER = "/identity/users/{id}";
    public static final String PATH_PUT_USER = "/identity/users/{id}";
    
    
}
