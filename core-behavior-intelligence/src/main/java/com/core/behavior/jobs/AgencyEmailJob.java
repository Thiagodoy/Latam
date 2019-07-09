/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.activiti.model.GroupMemberActiviti;
import com.core.activiti.model.UserActiviti;
import com.core.behavior.model.Agency;
import com.core.behavior.model.Notificacao;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.NotificacaoService;
import com.core.behavior.services.UserActivitiService;
import com.core.behavior.util.LayoutEmailEnum;
import com.core.behavior.util.Utils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author thiag
 */
public class AgencyEmailJob extends QuartzJobBean {

    @Autowired
    private UserActivitiService userActivitiService;

    @Autowired
    private FileService fileService;
    
    @Autowired
    private AgencyService agencyService;

    @Autowired
    private NotificacaoService notificacaoService;

    public static final String JOB_KEY_AGENCIA = "JOB_KEY_AGENCIA";

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

         System.out.println("hr ->" + LocalDateTime.now());
        Long id = jec.getJobDetail().getJobDataMap().getLong(JOB_KEY_AGENCIA);
        String[] profile = new String[]{"agência", "executivo de planejamento", "master agência"};

        boolean hasFile = fileService.hasFileToday(id);
        
       
        
        Agency agency =  agencyService.findById(id);

        if (hasFile) {
            return;
        }
        
        List<UserActiviti> users = null;

        try {
            PageRequest page =  PageRequest.of(0, 1000);
            users = userActivitiService.listAllUser(null, null, null, profile, new String[]{id.toString()}, page).getContent();    
        } catch (Exception e) {
            e.printStackTrace();
            users = new ArrayList<>();
        }
        

        users.forEach(u -> {

            Optional<GroupMemberActiviti> opt = u.getGroups().stream().filter(g -> g.getGroupId().equals("executivo de planejamento")).findFirst();

            Notificacao notificacao = new Notificacao();
            Map<String, String> parameter = new HashMap<String, String>();

            if (opt.isPresent()) {
                parameter.put(":email", u.getEmail());
                parameter.put(":nome", Utils.replaceAccentToEntityHtml(u.getFirstName()));
                parameter.put(":agencia", Utils.replaceAccentToEntityHtml(agency.getName()));
                notificacao.setParameters(Utils.mapToString(parameter));
                notificacao.setLayout(LayoutEmailEnum.ATIVO_EXECUTIVO);
            } else {
                parameter.put(":email", u.getEmail());
                parameter.put(":nome", Utils.replaceAccentToEntityHtml(u.getFirstName()));
                notificacao.setParameters(Utils.mapToString(parameter));
                notificacao.setLayout(LayoutEmailEnum.ATIVO);
            }

            notificacaoService.save(notificacao);

        });

    }

}
