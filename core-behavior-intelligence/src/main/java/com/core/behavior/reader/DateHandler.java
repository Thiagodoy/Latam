package com.core.behavior.reader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

/**
 *
 * @author thiag
 */
public class DateHandler implements TypeHandler {

    static SimpleDateFormat formatter1 = new SimpleDateFormat("ddMMyy",new Locale("pt","BR"));
    static SimpleDateFormat formatter2 = new SimpleDateFormat("ddMMyyyy",new Locale("pt","BR"));
    static SimpleDateFormat formatter3 = new SimpleDateFormat("MM/dd/yyyy",new Locale("pt","BR"));
    static SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy",new Locale("pt","BR"));
    static SimpleDateFormat formatter5 = new SimpleDateFormat("dd/MM/yy",new Locale("pt","BR"));
    
    
    static{
    
        formatter1.setLenient(false);
        formatter2.setLenient(false);
        formatter3.setLenient(false);
        formatter4.setLenient(false);
        formatter5.setLenient(false);
    
    }
    

    @Override
    public Object parse(String string) throws TypeConversionException {

        Object result = null;

        try {
            result = formatter1.parse(string);
        } catch (ParseException e) {
            try {
                result = formatter2.parse(string);
            } catch (ParseException e1) {
                try {
                    result = formatter3.parse(string);
                } catch (ParseException e2) {
                    try {
                        result = formatter4.parse(string);
                    } catch (ParseException e3) {
                        try {
                            result = formatter5.parse(string);
                        } catch (ParseException e4) {
                              throw new TypeConversionException("Data inválida!");
                        }
                    }
                }
            }
        }      

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
