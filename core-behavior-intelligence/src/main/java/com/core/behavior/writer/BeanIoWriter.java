/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.writer;

import com.core.behavior.jobs.IntegrationJob;
import com.core.behavior.util.Stream;
import com.core.behavior.util.TicketLayoutEnum;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
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

    
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_hh_mm_ss");
    
    
    public static <T> File writer(File directory,TicketLayoutEnum layout, T object,  Stream stream) {

        BeanWriter writer = null;
        Writer out = null;
        String dateNow = dateTimeFormatter.format(LocalDateTime.now());
        String nameFile = MessageFormat.format("integration_{0}_{1}.csv",dateNow, layout.toString());
        File file = new File(directory,nameFile);

        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream inputStream =   factory.getClass().getClassLoader().getResourceAsStream(stream.getStreamFile());
            
            factory.load(inputStream);

            out = new BufferedWriter(new FileWriter(file));
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
