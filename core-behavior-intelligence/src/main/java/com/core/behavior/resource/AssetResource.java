/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag  
 */
@RestController 
@RequestMapping(value = "/asset")
public class AssetResource {
    
    
    private String version = "v5.0.23";

    @GetMapping(value = "/download/image/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getFile(@PathVariable(name = "fileName") String fileName) throws IOException {
        InputStream in = getClass().getResourceAsStream("/static/" + fileName + ".png");
        return IOUtils.toByteArray(in);
    }
    
    @GetMapping(value = "/download/documents/{fileName}", produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getDocument(@PathVariable(name = "fileName") String fileName) throws IOException {
        InputStream in = getClass().getResourceAsStream("/static/" + fileName + ".pdf" );
        return IOUtils.toByteArray(in);
    }    
    
    @RequestMapping(value = "/versao",method = RequestMethod.GET)
    public ResponseEntity getVersao()throws IOException {
        return ResponseEntity.ok(this.version);
    }

}
