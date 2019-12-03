/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.services.SequenceService;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.SequenceTableEnum;
import com.core.behavior.util.Utils;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author thiag
 */
@DisallowConcurrentExecution
public class MonthlyJob extends QuartzJobBean {
    
    
    @Autowired
    private SequenceService sequenceService;

    private final int DATA_LIMIT = 30;
    private final int MONTH_LIMIT = 3; 

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        
        this.resetSequences();            

    }    
    
    private void resetSequences(){        
        sequenceService.resetSequence(SequenceTableEnum.TICKET);        
    }
    
    
    
   

}
