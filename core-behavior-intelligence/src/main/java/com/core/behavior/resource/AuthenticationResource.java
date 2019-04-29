package com.core.behavior.resource;

import com.core.behavior.exception.ActivitiException;
import com.core.behavior.request.ChangePasswordRequest;
import com.core.behavior.request.ForgotAcessRequest;
import com.core.behavior.request.LoginRequest;
import com.core.behavior.response.Response;
import com.core.behavior.response.UserResponse;
import com.core.behavior.services.UserActivitiService;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@RestController
@RequestMapping(value = "/auth")
public class AuthenticationResource {
    
    
    @Autowired
    private UserActivitiService service;
    
    @RequestMapping(value = "/change",method = RequestMethod.POST)
    @ApiOperation(value = "Change password")    
    public ResponseEntity saveUser(@RequestBody ChangePasswordRequest request){
        
        try {            
            service.changePassword(request);
            return ResponseEntity.ok().build();
        } catch (ActivitiException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);            
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }       
       
    } 

    
     
    @RequestMapping(value = "",method = RequestMethod.POST)
    @ApiOperation(value = "Login", response = UserResponse.class)   
    public ResponseEntity login(@RequestBody LoginRequest user){
        try {
            return ResponseEntity.ok(service.login(user));
        } catch (ActivitiException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(500).body(Response.build("Error", ex.getCodeMessage()));
        }
       
    }    
    
    
      
    @RequestMapping(value = "/forgot",method = RequestMethod.POST)
    @ApiOperation(value = "Send email with new password")   
    public ResponseEntity forgotAccess(@RequestBody ForgotAcessRequest request){
        try {            
            service.forgotAccess(request.getEmail());            
            return ResponseEntity.ok().build();
        } catch (ActivitiException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(500).body(Response.build("Error", ex.getCodeMessage()));
        } catch (MessagingException ex) {
            Logger.getLogger(AuthenticationResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(500).body(Response.build(ex.getMessage(), 500l));
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationResource.class.getName()).log(Level.SEVERE, null, ex);
             return ResponseEntity.status(500).body(Response.build(ex.getMessage(), 500l));
        }
       
    }    
    
}
