
package com.core.behavior.reader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

/**
 *
 * @author thiag
 */
public class DateHandler implements TypeHandler  {

    SimpleDateFormat formatter1 = new SimpleDateFormat("ddMMyy");
    SimpleDateFormat formatter2 = new SimpleDateFormat("ddMMyyyy");
    SimpleDateFormat formatter3 = new SimpleDateFormat("MM/dd/yyyy");
    
    @Override
    public Object parse(String string) throws TypeConversionException {
        
        if(string.isEmpty())
            return null;
        
        try {
            return formatter1.parse(string);
        } catch (ParseException ex) {
            try {
                return formatter2.parse(string);
            } catch (ParseException ex1) {
                try {
                    return formatter3.parse(string);
                } catch (ParseException ex2) {
                    return null;
                }
            }
        }
        
        
    }

    @Override
    public String format(Object o) {
        return "";
    }

    @Override
    public Class<?> getType() {
        return Date.class; 
    }
    
}
