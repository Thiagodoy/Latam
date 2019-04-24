package com.core.behavior.reader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

/**
 *
 * @author thiag
 */
public class DateHandler implements TypeHandler {

    static SimpleDateFormat formatter1 = new SimpleDateFormat("ddMMyy");
    static SimpleDateFormat formatter2 = new SimpleDateFormat("ddMMyyyy");
    static SimpleDateFormat formatter3 = new SimpleDateFormat("MM/dd/yyyy");
    static SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy");
    static SimpleDateFormat formatter5 = new SimpleDateFormat("dd/MM/yy");

    static List<SimpleDateFormat> fommaters = new ArrayList<>();

    static {
        fommaters.add(formatter1);
        fommaters.add(formatter2);
        fommaters.add(formatter3);
        fommaters.add(formatter4);
        fommaters.add(formatter5);
    }

    @Override
    public Object parse(String string) throws TypeConversionException {

        Object result = null;
        
        for (SimpleDateFormat f : fommaters ) {
            try {
                result = f.parse(string);
            } catch (ParseException ex) {}
        }
        
//        if(result == null){
//            throw new TypeConversionException(""); 
//        }
            
        return result;  
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
