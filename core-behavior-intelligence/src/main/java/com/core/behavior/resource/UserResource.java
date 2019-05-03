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
import com.core.behavior.services.UserInfoService;
import io.swagger.annotations.ApiOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private UserInfoService infoService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List users", response = PageResponse.class)
    public ResponseEntity listAllUser(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "userMaster", required = false) String userMaster,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size) {
        try {

            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));
            return ResponseEntity.ok(service.listAllUser(firstName, lastName, email, userMaster, pageRequest));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get a user by id", response = UserResponse.class)
    public ResponseEntity getUser(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(service.getUser(id));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }

    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a user")
    public ResponseEntity deleteUser(@RequestParam(name = "id", required = true) String id) {
        try {
            service.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (ActivitiException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), ex.getCodeMessage()));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }

    }

    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation(value = "Update a user")
    public ResponseEntity updateUser(@RequestBody UserRequest user) {
        try {
            service.updateUser(user);
            return ResponseEntity.ok().build();
        } catch (ActivitiException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), ex.getCodeMessage()));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Save a user")
    public ResponseEntity saveUser(@RequestBody UserRequest user) {

        try {
            service.saveUsers(user);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            service.deleteUser(user.getEmail());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }

    }

    @RequestMapping(value = "/userExists/{cpfCnpj}", method = RequestMethod.GET)
    @ApiOperation(value = "Verify if a user exists on data base")
    public ResponseEntity verifyCpfCnpj(@PathVariable("cpfCnpj") String cpfCnpj) {

        try {
            return ResponseEntity.ok(infoService.checkCpfCnpj(cpfCnpj));
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }

    }

    @RequestMapping(value = "/resendPassword", method = RequestMethod.GET)
    @ApiOperation(value = "Resend data of acess")
    public ResponseEntity resendAcess(@RequestParam(name = "email", required = true) String email, @RequestParam(name = "master", required = false) String isMaster) {

        try {
            service.resendAccess(email, Boolean.valueOf(isMaster));
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }

    }

}
