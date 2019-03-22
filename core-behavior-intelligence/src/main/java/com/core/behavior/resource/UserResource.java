/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import com.core.behavior.exception.ActivitiException;
import com.core.behavior.request.UserRequest;
import com.core.behavior.response.UserResponse;
import com.core.behavior.services.UserActivitiService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@RestController
@RequestMapping("/user")
@ApiOperation("UserResource")
public class UserResource {
    
    @Autowired
    private UserActivitiService service;
    
    @CrossOrigin(origins = "http://localhost:8002")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ApiOperation(value = "Login into application", response = UserResponse.class)
    public ResponseEntity login(@RequestBody UserRequest user){
        try {
            return ResponseEntity.ok(service.login(user));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(500).body(ex);
        }
       
    }
    
    @CrossOrigin(origins = "http://localhost:8002")
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "",response = UserResponse.class)
    public ResponseEntity getUsers(HttpServletRequest request){
        try {            
            return ResponseEntity.ok(service.listUsers(request.getQueryString()));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.ok("ERRROOOOOOOO");
        }
        
    }
    
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "Get only a user",response = UserResponse.class)
    public ResponseEntity getUser(@PathVariable("id") String id){
        try {            
            return ResponseEntity.ok(service.getUser(id));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.ok("ERRROOOOOOOO");
        }
        
    }
    
    @RequestMapping(value = "",method = RequestMethod.DELETE)
    @ApiOperation(value = "Login into application")
    public ResponseEntity deleteUser(){
        return null;
    }
    
    @RequestMapping(value = "",method = RequestMethod.PUT)
    @ApiOperation(value = "Login into application")
    public ResponseEntity updateUser(){
        return null;
    }
    
    @CrossOrigin(origins = "http://localhost:8002")
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Save a user")
    public ResponseEntity saveUser(@RequestBody UserRequest user){
        
        try {
            service.saveUser(user);
            return ResponseEntity.ok().build();
        } catch (ActivitiException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(ex.getCode()).body(ex);
        }       
       
    } 
    
    
}
