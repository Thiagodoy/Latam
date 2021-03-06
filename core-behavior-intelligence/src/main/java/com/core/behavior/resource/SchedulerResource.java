/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import com.core.behavior.services.SchedulerService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/scheduler")
public class SchedulerResource {
    
    @Autowired
    private SchedulerService service;
    
    @RequestMapping(value = "/generateFileIntegration/{fileId}",method = RequestMethod.POST)
    public ResponseEntity generateFileIntegration(@PathVariable("fileId")Long fileId){
        
        try {
            Logger.getLogger(SchedulerResource.class.getName()).log(Level.INFO, "[generateFileIntegration] ->" + fileId );
            service.processFile(fileId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(SchedulerResource.class.getName()).log(Level.SEVERE, "[generateFileIntegration] ->" + fileId, e );
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e);
        }
        
    }
    
    
}
