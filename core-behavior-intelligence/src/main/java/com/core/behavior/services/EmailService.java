package com.core.behavior.services;

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

        // use the true flag to indicate you need a multipart message
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

        // let's include the infamous windows Sample file (this time copied to c:/)
        File logo = Utils.loadLogo();
        FileSystemResource res = new FileSystemResource(logo);
        helper.addInline("identifier1234", res);
    
        sender.send(message);
        logo.delete();
    }
}
