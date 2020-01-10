/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior;


import com.core.behavior.jobs.IntegrationJob;
import com.core.behavior.services.IntegrationService;
import com.core.behavior.util.ThreadPoolFileIntegration;
import com.core.behavior.util.ThreadPoolFileValidation;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author thiag
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JobIntegrationTest {
    
    
     @Autowired
    private ThreadPoolFileValidation threadPoolFileValidation;
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private IntegrationService service;
    
    @Autowired
    private ThreadPoolFileIntegration threadPoolFileIntegration;
    
     
     @Test
     public void generateFileDuplicity(){
         
         
        // service.makeFileResultDataCollector(6158L);
         
         
     }
     
     
    //@Test
    public void process() throws IOException{
        
        
        
        
        
        IntegrationJob job = context.getBean(IntegrationJob.class);
                    job.setFileId(5480L);
                    threadPoolFileIntegration.submit(job);
                    
        threadPoolFileIntegration.getExecutor().shutdown();
        
        while(!threadPoolFileIntegration.getExecutor().isTerminated()){
        }
        
        
    }
    
}
