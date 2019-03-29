/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import com.core.behavior.activiti.response.PageResponse;
import com.core.behavior.exception.ActivitiException;
import com.core.behavior.request.LoginRequest;
import com.core.behavior.request.UserRequest;
import com.core.behavior.response.Response;
import com.core.behavior.response.UserResponse;
import com.core.behavior.services.UserActivitiService;
import io.swagger.annotations.ApiOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/user")
@ApiOperation("UserResource")
public class UserResource {
    
    @Autowired
    private UserActivitiService service;
    
    
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ApiOperation(value = "Login", response = UserResponse.class)
    @CrossOrigin(origins = "http://localhost:8002") 
    public ResponseEntity login(@RequestBody LoginRequest user){
        try {
            return ResponseEntity.ok(service.login(user));
        } catch (ActivitiException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(500).body(Response.build("Error", ex.getCodeMessage()));
        }
       
    }    
    
    
    
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List users",response = PageResponse.class)
    @CrossOrigin(origins = "http://localhost:8002") 
    public ResponseEntity listAllUser(
            @RequestParam(name = "firstName",required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size){
        try {            
            
            PageRequest pageRequest = PageRequest.of(page, size,Sort.by("id"));
            return ResponseEntity.ok(service.listAllUser(firstName,lastName,email,pageRequest));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(500).body(ex);
        }
        
    }
    
    
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "Get a user by id",response = UserResponse.class)
    @CrossOrigin(origins = "http://localhost:8002") 
    public ResponseEntity getUser(@PathVariable("id") String id){
        try {            
            return ResponseEntity.ok(service.getUser(id));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(500).body(ex);
        }
        
    }
    
    
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a user")
    @CrossOrigin(origins = "http://localhost:8002") 
    public ResponseEntity deleteUser(@PathVariable("id") String id){
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    
    
    @RequestMapping(value = "",method = RequestMethod.PUT)
    @ApiOperation(value = "Update a user")
    @CrossOrigin(origins = "http://localhost:8002") 
    public ResponseEntity updateUser(@RequestBody  UserRequest user){
        service.updateUser(user);
        return ResponseEntity.ok().build();
    }
    
    
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Save a user")
    @CrossOrigin(origins = "http://localhost:8002") 
    public ResponseEntity saveUser(@RequestBody UserRequest user){
        
        try {
            service.saveUsers(user);
            return ResponseEntity.ok().build();
        } catch (ActivitiException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            service.deleteUser(user.getEmail());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(null, ex.getCodeMessage()));
        }       
       
    } 
    
    
}
