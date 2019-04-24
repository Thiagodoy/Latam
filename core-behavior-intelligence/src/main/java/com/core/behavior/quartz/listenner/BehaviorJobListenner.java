package com.core.behavior.quartz.listenner;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class BehaviorJobListenner implements JobListener{

    @Override
    public String getName() {
        return "BehaviorJobListenner";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jec) {
        System.out.println("JOB WILL EXECUTED" + jec.getJobDetail().getKey().getName());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jec) {
        
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jec, JobExecutionException jee) {
         System.out.println("JOB WILL EXECUTED" + jec.getJobDetail().getKey().getName());       
    }

}
