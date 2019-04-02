package com.core.behavior.configuration;

import com.core.behavior.jobs.ProcessFileJob;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
public class QuartzConfiguration {

    
    public QuartzConfiguration(SchedulerFactoryBean bean) throws SchedulerException {

        Scheduler scheduler = bean.getScheduler();
        
        
        
        
       JobDetail detail =  JobBuilder
                .newJob(ProcessFileJob.class)
                .withIdentity("JOB-PARSE-CSVC")
                .withDescription("Processing files")
                .build();
        
        CronTrigger crontrigger = TriggerBuilder.newTrigger().withIdentity("teste", "crongroup1")
                .withSchedule(cronSchedule("0 0/1 * 1/1 * ? *").withMisfireHandlingInstructionFireAndProceed()).build();
        
        scheduler.scheduleJob(detail, crontrigger);





    }
    
}
