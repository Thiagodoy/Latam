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
       
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jec) {
        
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jec, JobExecutionException jee) {
        JobKey jobkey = jec.getJobDetail().getKey();
        try {
            boolean jobDeleted = jec.getScheduler().deleteJob(jobkey);
            System.out.println("deletado -> " + jobDeleted);
        } catch (SchedulerException ex) {
            Logger.getLogger(BehaviorJobListenner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
