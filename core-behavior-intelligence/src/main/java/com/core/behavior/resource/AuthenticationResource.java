package com.core.behavior.resource;

import com.core.behavior.exception.ApplicationException;
import com.core.behavior.request.ChangePasswordRequest;
import com.core.behavior.request.ForgotAcessRequest;
import com.core.behavior.request.LoginRequest;
import com.core.behavior.response.Response;
import com.core.behavior.response.UserResponse;
import com.core.behavior.services.UserActivitiService;
import com.core.behavior.services.UserInfoService;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    
    @Autowired
    private UserInfoService infoService;
    
    @RequestMapping(value = "/change",method = RequestMethod.POST)
    @ApiOperation(value = "Change password")    
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest request){
        
        try {            
            service.changePassword(request);
            return ResponseEntity.ok().build();
        } catch (ApplicationException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);            
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }       
       
    } 

    
     
    @RequestMapping(value = "",method = RequestMethod.POST)
    @ApiOperation(value = "Login", response = UserResponse.class)   
    public ResponseEntity login(@RequestBody LoginRequest user){
        try {
            return ResponseEntity.ok(service.login(user));
        } catch (ApplicationException ex) {            
            return ResponseEntity.status(500).body(Response.build("Error", ex.getCodeMessage()));
        }
       
    }    
    
    
      
    @RequestMapping(value = "/forgot",method = RequestMethod.POST)
    @ApiOperation(value = "Send email with new password")   
    public ResponseEntity forgotAccess(@RequestBody ForgotAcessRequest request){
        try {            
            service.forgotAccess(request.getEmail());            
            return ResponseEntity.ok().build();
        } catch (ApplicationException ex) {            
            return ResponseEntity.status(500).body(Response.build("Error", ex.getCodeMessage()));
        } catch (MessagingException ex) {
            
            return ResponseEntity.status(500).body(Response.build(ex.getMessage(), 500l));
        } catch (IOException ex) {
            
             return ResponseEntity.status(500).body(Response.build(ex.getMessage(), 500l));
        }
       
    }    
    
    @RequestMapping(value = "/expired",method = RequestMethod.POST)
    @ApiOperation(value = "")   
    public ResponseEntity ExpiredPassword(@RequestParam("email")String email){
        try {            
            infoService.expiredPassword(email);            
            return ResponseEntity.ok().build();
        } catch (ApplicationException ex) {            
            return ResponseEntity.status(500).body(Response.build("Error", ex.getCodeMessage()));
        }  catch (Exception ex) {            
             return ResponseEntity.status(500).body(Response.build(ex.getMessage(), 500l));
        }
            
    }    
    
}
