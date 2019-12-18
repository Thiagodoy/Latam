/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior;

import com.core.behavior.services.FileService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author thiag
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TesteDownloadS3 {
    
    @Autowired
    private FileService fileService;
    
   // @Test
    public void download(){
        try {
            fileService.downloadFile("Confianca_201812.csv", 43L, true);
        } catch (IOException ex) {
            Logger.getLogger(TesteDownloadS3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
