/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import com.core.behavior.response.UserResponse;
import com.core.behavior.services.ActivitiService;
import io.swagger.annotations.ApiOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    private ActivitiService service;
    
    
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ApiOperation(value = "Login into application")
    public ResponseEntity login(){
        return null;
    }
    
    @RequestMapping(value = "",method = RequestMethod.GET)
    @ApiOperation(value = "")
    public ResponseEntity getUsers(){
        try {
            ;
            return ResponseEntity.ok(service.listUsers());
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.ok("ERRROOOOOOOO");
        }
        
    }
    
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "Get only a user",response = UserResponse.class)
    public ResponseEntity getUser(@PathVariable("id") String id){
        try {
            ;
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
    
    @RequestMapping(value = "",method = RequestMethod.POST)
    @ApiOperation(value = "Login into application")
    public ResponseEntity saveUser(){
        return null;
    } 
    
    
}
