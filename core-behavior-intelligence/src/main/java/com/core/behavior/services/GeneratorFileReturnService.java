/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.dto.LineErrorDTO;
import com.core.behavior.exception.ApplicationException;
import com.core.behavior.jobs.FileReturnJob;
import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.model.Agency;
import com.core.behavior.model.Log;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.repository.LogRepository;
import com.core.behavior.util.MessageCode;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
