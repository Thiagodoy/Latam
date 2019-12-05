/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.io;

import com.core.behavior.jobs.IntegrationJob;
import com.core.behavior.util.Stream;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketTypeEnum;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

/**
 *
 * @author thiag
 */
public class BeanIoWriter {

    
    
    
    
    public static <T> File writer(File file,TicketLayoutEnum layout, T object,  Stream stream) {

        BeanWriter writer = null;
        Writer out = null;      
      

        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream inputStream =   factory.getClass().getClassLoader().getResourceAsStream(stream.getStreamFile());
            
            factory.load(inputStream);

            
            out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);            
            writer = factory.createWriter(stream.getStreamId(), out);            
            writer.write(object);           

        } catch (Exception ex) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, null, ex);            
        } finally {
            if (writer != null) {
                writer.flush();
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(BeanIoWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
                writer.close();
            }
        }

        return file;

    }

}
