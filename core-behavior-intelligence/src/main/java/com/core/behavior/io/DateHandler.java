package com.core.behavior.io;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

/**
 *
 * @author thiag
 */
public class DateHandler implements TypeHandler {

    static SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
    static SimpleDateFormat formatter5 = new SimpleDateFormat("dd/MM/yy", new Locale("pt", "BR"));

    static {
        formatter4.setLenient(false);
        formatter5.setLenient(false);
    }

    @Override
    public Object parse(String string) throws TypeConversionException {

        Object result = null;

        
        if(string.length()!= 10 || string.length()!= 8){
             throw new TypeConversionException("Data inválida!");
        }
        
        try {
            result = string.length() == 10 ? formatter4.parse(string) : formatter5.parse(string);
        } catch (ParseException e3) {
            throw new TypeConversionException("Data inválida!");
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
