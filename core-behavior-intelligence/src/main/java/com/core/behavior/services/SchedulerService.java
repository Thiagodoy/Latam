/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.jobs.IntegrationJob;
import com.core.behavior.util.ThreadPoolFileIntegration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class SchedulerService {

    @Autowired
    private ThreadPoolFileIntegration threadPoolFileIntegration;

    @Autowired
    private ApplicationContext context;

    public void processFile(Long id) {

        IntegrationJob job = context.getBean(IntegrationJob.class);
        job.setFileId(id);
        Logger.getLogger(SchedulerService.class.getName()).log(Level.INFO,"Inicializando a execução do job de integraçao");
        threadPoolFileIntegration.getExecutor().submit(job);
        
        
    }

}
