package com.core.behavior.reader;

import com.core.behavior.model.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class BeanIoReader {

    
     private final String streamId = "flytour";
    private final String xmlParser = "static/FILE-PARSER.xml";
    
    public <T> List<T> parse(File file, String str){
    
        
        StreamFactory factory = StreamFactory.newInstance();
        InputStream stream = factory.getClass().getClassLoader().getResourceAsStream(xmlParser);
        factory.load(str);
        
        java.io.File f = new java.io.File("");
        BeanReader reader = factory.createReader(str, f);

        List<T> list = new ArrayList<T>();
        
        
        T record = null;
        
        
        while((record = (T)reader.read())!= null){        
         list.add(record);        
        }
        
        reader.close();
        
        return list;
    }
    
}
