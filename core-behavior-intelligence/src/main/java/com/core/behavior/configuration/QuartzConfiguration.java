package com.core.behavior.configuration;

import com.core.behavior.jobs.AgenciaFactoryJob;
import com.core.behavior.jobs.ConsumerEmailJob;
import com.core.behavior.quartz.listenner.BehaviorJobListenner;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.quartz.impl.matchers.GroupMatcher;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
public class QuartzConfiguration {

    public QuartzConfiguration(SchedulerFactoryBean bean) throws SchedulerException {

        Scheduler scheduler = bean.getScheduler();
        
        scheduler.getListenerManager().addJobListener(new BehaviorJobListenner(), GroupMatcher.jobGroupEquals("fg_jobgroup_01"));
        
        scheduler.start();

        JobDetail detail = JobBuilder.newJob(ConsumerEmailJob.class).withIdentity("ConsumerEmailJob", "consumer-email")
                .withDescription("Sender email")
                .build();
        CronTrigger crontrigger = TriggerBuilder.newTrigger().withIdentity("ConsumerEmailJobTrigger", "consumer-email")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *")
                .withMisfireHandlingInstructionFireAndProceed())
                .build();
        scheduler.scheduleJob(detail, crontrigger);
        
        
        JobDetail detail1 = JobBuilder.newJob(AgenciaFactoryJob.class).withIdentity("AgenciaFactoryJob", "agencia-factory-email")
                .withDescription("Agendador de notificação de email")
                .build();
        CronTrigger crontrigger1 = TriggerBuilder.newTrigger().withIdentity("AgenciaFactoryJob", "agencia-factory-email")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 34 13 1/1 * ? *")
                .withMisfireHandlingInstructionFireAndProceed())
                .build();

        scheduler.scheduleJob(detail1, crontrigger1);
    }

}
