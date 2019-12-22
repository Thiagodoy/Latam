/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior;

import com.core.behavior.jobs.FileReturnJob;
import com.core.behavior.jobs.FileReturnJob1;
import com.core.behavior.util.ThreadPoolFileReturn;
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
public class FileReturnTest {
    
    
    
    @Autowired
    private ThreadPoolFileReturn threadPoolFileExecutor;
    
    
    @Autowired
    private ApplicationContext context;
    
    
    @Test
    public void generateFile(){
        
        FileReturnJob1 fileReturnJob = this.context.getBean(FileReturnJob1.class);
        
        fileReturnJob.setParameter(FileReturnJob.DATA_FILE_ID, 5648L);
        fileReturnJob.setParameter(FileReturnJob.DATA_EMAIL_ID, "thiagodoy@hotmail.com");        
        threadPoolFileExecutor.getExecutor().submit(fileReturnJob);
        
        threadPoolFileExecutor.getExecutor().shutdown();
        while(!threadPoolFileExecutor.getExecutor().isTerminated()){}
        
        
        
    }
}
