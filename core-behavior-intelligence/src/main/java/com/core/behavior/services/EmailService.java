package com.core.behavior.services;

import com.core.behavior.util.Constantes;
import com.core.behavior.util.EmailLayoutEnum;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender sender;

    public void send(EmailLayoutEnum layout,String subject,Map<String,String>parameters, String... emails) throws MessagingException, IOException {

        MimeMessage message = sender.createMimeMessage();        
        MimeMessageHelper helper = new MimeMessageHelper(message, true);        
        String content = Utils.getContenFromLayout(layout);      
        
        helper.setTo(emails);
        helper.setSubject(subject);
        
        
        
        if(parameters != null && parameters.size() > 0){            
            for (String key : parameters.keySet()) {
                content = content.replace(key, parameters.get(key));
            }
        }        

        
        helper.setText(content, true);        
        
        File logo = Utils.loadLogo(Constantes.IMAGE_CHANFRO);
        FileSystemResource res = new FileSystemResource(logo);
        helper.addInline("identifier1", res);
        
        File logo1 = Utils.loadLogo(Constantes.IMAGE_FUNDO);
        FileSystemResource res1 = new FileSystemResource(logo1);
        helper.addInline("identifier2", res1);
        
        File logo2 = Utils.loadLogo(Constantes.IMAGE_LOGO_001);
        FileSystemResource res2 = new FileSystemResource(logo2);
        helper.addInline("identifier3", res2);
        
        File logo3 = Utils.loadLogo(Constantes.IMAGE_LOGO_002);
        FileSystemResource res3 = new FileSystemResource(logo3);
        helper.addInline("identifier4", res3);
        
        File logo4 = Utils.loadLogo(Constantes.IMAGE_LOGO_LATAM);
        FileSystemResource res4 = new FileSystemResource(logo4);
        helper.addInline("identifier5", res4);
        
        File logo5 = Utils.loadLogo(Constantes.IMAGE_LOGO_ONE);
        FileSystemResource res5 = new FileSystemResource(logo5);
        helper.addInline("identifier6", res5);
        
    
        sender.send(message);
        logo.delete();
        logo1.delete();
        logo2.delete();
        logo3.delete();
        logo4.delete();
        logo5.delete();
    }
}
