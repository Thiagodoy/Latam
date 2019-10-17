package com.core.behavior;

import com.core.behavior.jobs.FileReturnJob;
import com.core.behavior.services.TicketService;
import java.util.Date;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BehaviorApplicationTests {

    @Autowired
    private TicketService service;

    @Autowired
    private SchedulerFactoryBean bean;

    @Test
    public void contextLoads() throws SchedulerException, InterruptedException {

        JobDataMap data = new JobDataMap();
        data.put(FileReturnJob.DATA_FILE_ID, 28L);
        data.put(FileReturnJob.DATA_EMAIL_ID, "thiagodoy@hotmail.com");

        JobDetail detail = JobBuilder
                .newJob(FileReturnJob.class)
                .withIdentity("FILE-RETURN-JOB-" + String.valueOf(28), "process-file-return")
                .withDescription("Processing Return file")
                .usingJobData(data)
                .build();

        SimpleTrigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("FILE-RETURN-TRIGGER-" + String.valueOf(28), "process-file-return")
                .startAt(new Date())
                .withSchedule(simpleSchedule())
                .build();
        
        bean.getScheduler().scheduleJob(detail, trigger);
        
        
        Optional<JobExecutionContext> opt = null;
        do{
             Thread.sleep(55000);
            opt = bean.getScheduler().getCurrentlyExecutingJobs().stream().filter((jj) -> jj.getJobDetail().getKey().getName().equals(detail.getKey().getName())).findFirst();
        }while(opt.isPresent());
        
       

        
        
        
    }

}
