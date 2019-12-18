package com.core.behavior;

import com.core.behavior.services.IntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BehaviorApplicationTests {

    
    
    @Autowired
    private IntegrationService service;
    
    //@Test
    public void testFileResult(){
        service.makeFileResultDataCollector(122L);
    }

//    @Test
    public void contextLoads() throws SchedulerException, InterruptedException {

//        JobDataMap data = new JobDataMap();
//        data.put(FileReturnJob.DATA_FILE_ID, 28L);
//        data.put(FileReturnJob.DATA_EMAIL_ID, "thiagodoy@hotmail.com");
//
//        JobDetail detail = JobBuilder
//                .newJob(FileReturnJob.class)
//                .withIdentity("FILE-RETURN-JOB-" + String.valueOf(28), "process-file-return")
//                .withDescription("Processing Return file")
//                .usingJobData(data)
//                .build();
//
//        SimpleTrigger trigger = TriggerBuilder
//                .newTrigger()
//                .withIdentity("FILE-RETURN-TRIGGER-" + String.valueOf(28), "process-file-return")
//                .startAt(new Date())
//                .withSchedule(simpleSchedule())
//                .build();
//
//        bean.getScheduler().scheduleJob(detail, trigger);
//
//        Optional<JobExecutionContext> opt = null;
//        do {
//            Thread.sleep(55000);
//            opt = bean.getScheduler().getCurrentlyExecutingJobs().stream().filter((jj) -> jj.getJobDetail().getKey().getName().equals(detail.getKey().getName())).findFirst();
//        } while (opt.isPresent());

    }

  

}
