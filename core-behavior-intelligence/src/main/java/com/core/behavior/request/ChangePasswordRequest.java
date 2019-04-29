package com.core.behavior.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    
    @Getter
    @Setter
    private String email;
    
    @Getter
    @Setter
    private String password;
    
    @Getter
    @Setter
    private String newPassword;  
    
    private boolean firstAccess;
    
    public boolean isFirstAccess(){
        return this.firstAccess;
    }
    
    public void setFirstAcess(boolean value){
        this.firstAccess = value;
    }

}
