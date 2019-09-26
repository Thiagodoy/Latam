/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.exception.ApplicationException;
import com.core.behavior.jobs.FileReturnJob;
import com.core.behavior.model.Agency;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.util.MessageCode;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author thiag
 */
@Service
public class GeneratorFileReturnService {    
    
    @Autowired
    private SchedulerFactoryBean bean;
    
    
    @Autowired
    private ClientAws clientAws;
    
    
    @Autowired
    private AgencyRepository agencyRepository;

    
    
    public File downloadFileReturn(Long idAgencia, String fileName) throws IOException{
        
        final Agency agency = agencyRepository.findById(idAgencia).get();
        
        String folder  = agency.getS3Path().split("\\\\")[1];
        
        File file = clientAws.downloadFileReturn(fileName, folder);
        
        return file;
    }
    
    
    public void generateFileReturnFriendly(Long id, String email) throws Exception {
        
        
        
        JobDataMap data = new JobDataMap();
        data.put(FileReturnJob.DATA_FILE_ID, id);
        data.put(FileReturnJob.DATA_EMAIL_ID, email);
        

        JobDetail detail = JobBuilder
                .newJob(FileReturnJob.class)
                .withIdentity("FILE-RETURN-JOB-" + String.valueOf(id), "process-file-return")
                .withDescription("Processing Return file")
                .usingJobData(data)
                .build();

        SimpleTrigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("FILE-RETURN-TRIGGER-" + String.valueOf(id), "process-file-return")
                .startAt(new Date())
                .withSchedule(simpleSchedule())
                .build();

        
        
        Optional<JobExecutionContext> opt =  bean.getScheduler().getCurrentlyExecutingJobs().stream().filter((jj)->jj.getJobDetail().getKey().getName().equals(detail.getKey().getName())).findFirst();
        
        if(opt.isPresent()){
            throw new ApplicationException(MessageCode.JOB_IS_RUNNING);
        }else{
            bean.getScheduler().scheduleJob(detail, trigger);
        }
        
        
    }

    

}
