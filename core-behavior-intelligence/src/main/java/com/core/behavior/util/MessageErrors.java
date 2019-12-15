/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;

import com.core.behavior.validator.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.beanio.StreamFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class MessageErrors {
    
    private static Properties props = new Properties();
    private static ConcurrentHashMap<String, String> messages = new  ConcurrentHashMap<String, String>(50);
    
    
    static {
        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream is = factory.getClass().getClassLoader().getResourceAsStream("beanio/layoutMinimoMessages.properties");
            props.load(is);
            
            for(String key : props.stringPropertyNames()){
                messages.put(key, props.getProperty(key));
            }            
        } catch (IOException ex) {
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static ConcurrentHashMap<String, String> getMessages(){
        return messages;
    }
    
    
}
