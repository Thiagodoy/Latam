package com.core.behavior.configuration;

import com.core.behavior.jobs.AgenciaFactoryJob;
import com.core.behavior.jobs.ConsumerEmailJob;
import com.core.behavior.jobs.IntegrationJob;
import com.core.behavior.quartz.listenner.BehaviorJobListenner;
import javax.sql.DataSource;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
@EnableAutoConfiguration
public class QuartzConfiguration {

    
    
    
//    @Bean
//    @QuartzDataSource
//    public DataSource quartzDataSource() {
//        return DataSourceBuilder.create().build();
//    }
    
    public QuartzConfiguration(SchedulerFactoryBean bean) throws SchedulerException {

        Scheduler scheduler = bean.getScheduler();

        scheduler.getListenerManager().addJobListener(new BehaviorJobListenner(), GroupMatcher.jobGroupEquals("fg_jobgroup_01"));

        scheduler.start();

        JobDetail detail = JobBuilder.newJob(ConsumerEmailJob.class).withIdentity("ConsumerEmailJob", "consumer-email")
                .withDescription("Sender email")
                .build();
        CronTrigger crontrigger = TriggerBuilder.newTrigger().withIdentity("ConsumerEmailJobTrigger", "consumer-email")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/3 * 1/1 * ? *")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
        scheduler.scheduleJob(detail, crontrigger);

        JobDetail detail1 = JobBuilder.newJob(AgenciaFactoryJob.class).withIdentity("AgenciaFactoryJob", "agencia-factory-email")
                .withDescription("Agendador de notificação de email")
                .build();
        CronTrigger crontrigger1 = TriggerBuilder.newTrigger().withIdentity("AgenciaFactoryJob", "agencia-factory-email")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 1/1 * ? *")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();

        scheduler.scheduleJob(detail1, crontrigger1);
        
        //:FIXME
        JobDetail detail2= JobBuilder.newJob(IntegrationJob.class).withIdentity("IntegrationJob", "integration-job")
                .withDescription("Integrador")
                .build();
        CronTrigger crontrigger2 = TriggerBuilder.newTrigger().withIdentity("IntegrationJob", "integration-job")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/3 * 1/1 * ? *")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();

        //scheduler.scheduleJob(detail2, crontrigger2);
    }

}
