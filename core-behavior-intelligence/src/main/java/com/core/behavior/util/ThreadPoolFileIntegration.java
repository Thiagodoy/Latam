/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;

import com.core.behavior.jobs.IntegrationJob;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ThreadPoolFileIntegration {
     private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
     
     
     public synchronized void submit(IntegrationJob job){
         
         Optional<IntegrationJob> integrationJob =  executor.getQueue().stream().map(q->{
             return (IntegrationJob)q;
         }).filter(i-> i.getFileId().equals(job.getFileId())).findFirst();
         
         if(!integrationJob.isPresent()){
             executor.submit(job);
         }
         
         
     }
     
     
}
