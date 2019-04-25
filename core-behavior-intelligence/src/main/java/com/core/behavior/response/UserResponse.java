package com.core.behavior.response;


import com.core.activiti.model.UserActiviti;
import com.core.activiti.model.UserInfo;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import lombok.Data;

//import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@ApiModel(description = "Represents a user of application")
public class UserResponse implements Serializable{ 
    
    private String id;
    private String firstName;
    private String lastName;
    private String url;
    private String email;
    private String pictureUrl; 
    private boolean isAuthenticated;
    
    private List<GroupResponse> groups;
    private List<UserInfo> info;
    
    public UserResponse(){}
    
    public UserResponse(UserActiviti entity){
        this.id = entity.getId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.url = null;
        this.email = entity.getEmail();        
        this.pictureUrl = entity.getPicture();
        this.info = entity.getInfo();
    }    
    
}
