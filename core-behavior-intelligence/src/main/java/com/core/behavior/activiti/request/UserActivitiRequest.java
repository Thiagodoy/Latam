package com.core.behavior.activiti.request;

import com.core.behavior.request.UserRequest;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class UserActivitiRequest {

    private String id;
    private String firstName;
    private String lastName;   
    private String email;   
    private String password; 
    
    public UserActivitiRequest(UserRequest user){
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}
