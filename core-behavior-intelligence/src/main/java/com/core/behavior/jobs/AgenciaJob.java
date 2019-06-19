/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.activiti.model.GroupMemberActiviti;
import com.core.activiti.model.UserActiviti;
import com.core.behavior.services.EmailService;
import com.core.behavior.services.UserActivitiService;
import java.util.List;
import java.util.Optional;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author thiag
 */
public class AgenciaJob extends QuartzJobBean{

    
    @Autowired
    private UserActivitiService userActivitiService;
    
    @Autowired
    private EmailService emailService;
    
    public static final String JOB_KEY_AGENCIA = "JOB_KEY_AGENCIA";
    
    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
    
        String agencia = jec.getJobDetail().getJobDataMap().getString(JOB_KEY_AGENCIA);
        String[]profile = new String[]{"agência","executivo de planejamento","master agência"};
        
        List <UserActiviti> users = userActivitiService.listAllUser(null, null, null, profile, new String[]{agencia}, null).getContent();        
        
        users.forEach(u->{
            
            Optional<GroupMemberActiviti> opt = u.getGroups().stream().filter(g-> g.getGroupId().equals("executivo de planejamento")).findFirst();
            
            if(opt.isPresent()){
                
            }else{
                
            }
            
        });
        
        
        
        
        
    }
    
}
