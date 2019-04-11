package com.core.behavior.jobs;

import com.core.behavior.services.TicketService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@DisallowConcurrentExecution
public class DuplicityJob extends QuartzJobBean{
    
    public static final String DATA_FILE_ID = "fileId";
    
    @Autowired
    private TicketService service;

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        
        Long fileId = jec.getJobDetail().getJobDataMap().getLong(DATA_FILE_ID);
        
        service.listDuplicityByFileId(fileId).parallelStream().forEach(t->{
        
        
        
        
        
        
        });
        
        
        
        
    }

}
