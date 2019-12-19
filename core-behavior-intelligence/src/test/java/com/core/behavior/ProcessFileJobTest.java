/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior;


import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.jobs.ProcessFileJob1;


import com.core.behavior.model.Agency;
import com.core.behavior.services.AgencyService;
import com.core.behavior.util.ThreadPoolFileValidation;
import java.io.File;
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
public class ProcessFileJobTest {
    
    
     @Autowired
    private ThreadPoolFileValidation threadPoolFileValidation;
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private AgencyService agencyService;    

    @Autowired
    private ClientAws clientAws;
    
    @Test
    public void process() throws IOException{
        
        
         Agency agency = agencyService.findById(25l);
        String folder = true ? agency.getS3Path().split("\\\\")[1] + "/ORIGINAL" : agency.getS3Path().split("\\\\")[1];

        File file =  new File("teste_thiago.csv");//clientAws.downloadFile("MASTER 28-10-19 a 03-11-19.csv.csv", folder);
        
        
        
        
//        
//        FileWriter writer = new FileWriter(new File("writer.txt"));
//        
//        writer.append("Teste");
//        writer.flush();
//        writer.close();
          
        ProcessFileJob1 processFileJob = context.getBean(ProcessFileJob1.class);
        processFileJob.setParameter(ProcessFileJob.DATA_USER_ID, "thiagodoy@hotmail.com");
        processFileJob.setParameter(ProcessFileJob.DATA_COMPANY, 25L);
        
        
        
        processFileJob.setParameter(ProcessFileJob.DATA_FILE, file);
        processFileJob.setParameter(ProcessFileJob.DATA_FILE_ID, 5504L);
        processFileJob.setParameter(ProcessFileJob.DATA_LAYOUT_FILE, 2L);

        threadPoolFileValidation.getExecutor().submit(processFileJob);
        threadPoolFileValidation.getExecutor().shutdown();
        
        while(!threadPoolFileValidation.getExecutor().isTerminated()){
        }
        
        
    }
    
}
