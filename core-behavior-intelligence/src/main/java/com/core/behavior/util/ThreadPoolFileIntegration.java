/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;

import com.core.behavior.jobs.IntegrationJob;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;

/**
 *
 * @author thiagS
 */
@Data
public class ThreadPoolFileIntegration {

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private Map<Long, IntegrationJob> poll = Collections.synchronizedMap(new HashMap<Long, IntegrationJob>());

    public synchronized void submit(IntegrationJob job) {

        
        
        
        synchronized (poll) {
            boolean isScheduled = poll.containsKey(job.getFileId());
            
            Logger.getLogger(ThreadPoolFileIntegration.class.getName()).log(Level.INFO, "Adiciona no poll -> " + !isScheduled );
            

            if (!isScheduled) {
                poll.put(job.getFileId(), job);
                job.setPool(poll);
                executor.submit(job);
                Logger.getLogger(ThreadPoolFileIntegration.class.getName()).log(Level.INFO, "Adicionado");

            }

        }

    }

}
