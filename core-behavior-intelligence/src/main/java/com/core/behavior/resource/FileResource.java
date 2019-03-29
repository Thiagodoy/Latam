/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import io.swagger.annotations.ApiOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@RestController
@RequestMapping("/file")
public class FileResource {
    
    
    @RequestMapping(value = "",method = RequestMethod.POST)    
    @ApiOperation(value = "Upload of files")
    @CrossOrigin(origins = "http://localhost:8002")    
    public ResponseEntity uploadFile(
//            @PathVariable Long companyId,
            @RequestPart(value = "file") MultipartFile file){ 
    
        try {           
            return ResponseEntity.ok("File Uploaded success!");
        } catch (Exception ex) {            
            return ResponseEntity.status(500).body(ex.getMessage());
        }        
          
    }
    
}
