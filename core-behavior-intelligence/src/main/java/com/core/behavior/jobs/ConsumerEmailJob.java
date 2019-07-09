/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.model.Notificacao;
import com.core.behavior.repository.NotificacaoRepository;
import com.core.behavior.util.NotificacaoStatusEnum;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.beanio.StreamFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author thiag
 */
@DisallowConcurrentExecution
public class ConsumerEmailJob extends QuartzJobBean {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private JavaMailSender sender;

    @Transactional
    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        PageRequest page = PageRequest.of(0, 5);
        List<Notificacao> notificacaos = notificacaoRepository.findByStatus(NotificacaoStatusEnum.READY, page);

        notificacaos.forEach(n -> {

            MimeMessage message = sender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                Map<String, String> parameters = Utils.toMap(n.getParameters());
                String content = this.getContentEmail(n.getLayout().getPath());
               
                helper.setSubject(n.getLayout().getSubject());

                if (parameters != null && parameters.size() > 0) {
                    for (String key : parameters.keySet()) {
                        content = content.replace(key, parameters.get(key));
                        if (key.equals(":email")) {
                            helper.setTo(parameters.get(key).split(";"));
                        }
                    }
                }
                
                content = Utils.replaceAccentToEntityHtml(content);
                
                helper.setText(content, true); 
                
                Map<String, String> map = n.getLayout().getResouces();
                Set<String> keys = map.keySet();

                for (String key : keys) {
                    File logo = Utils.loadLogo(map.get(key));
                    FileSystemResource res = new FileSystemResource(logo);
                    helper.addInline(key, res);

                }

                sender.send(message);

                n.setStatus(NotificacaoStatusEnum.SENT);
                notificacaoRepository.save(n);

            } catch (Exception ex) {
                Logger.getLogger(ConsumerEmailJob.class.getName()).log(Level.SEVERE, null, ex);

                long retry = n.getRetry();
                ++retry;
                n.setRetry(retry);
                if (n.getRetry() > 2) {
                    n.setErro(ex.getMessage());
                    n.setStatus(NotificacaoStatusEnum.ERROR);
                }
                notificacaoRepository.save(n);
            }

        });

    }

    private String getContentEmail(String path) throws IOException {
        StreamFactory factory = StreamFactory.newInstance();
        InputStream stream = factory.getClass().getClassLoader().getResourceAsStream(path);
        return IOUtils.toString(stream, "UTF-8");
    }

}
