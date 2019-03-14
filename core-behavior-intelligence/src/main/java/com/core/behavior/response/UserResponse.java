package com.core.behavior.response;


import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class UserResponse implements Serializable{ 
    private String id;
    private String firstName;
    private String lastName;
    private String url;
    private String email;
    private String pictureUrl;
    private String password;
    
}
