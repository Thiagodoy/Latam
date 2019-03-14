package com.core.behavior.request;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class UserRequest implements Serializable { 
    private String id;
    private String firstName;
    private String lastName;   
    private String email;   
    private String password;
    
}
