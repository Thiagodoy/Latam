package com.core.behavior.reader;

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

    static {
        formatter4.setLenient(false);
    }

    @Override
    public Object parse(String string) throws TypeConversionException {

        Object result = null;

        try {
            result = formatter4.parse(string);
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
