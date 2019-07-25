/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.reader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class DoubleHandler implements TypeHandler {

    @Override
    public Object parse(String string) throws TypeConversionException {

        try {
            Pattern p = Pattern.compile("^([0-9]{1,3}\\.?)+(,[0-9]{1,2})?$");
            Matcher m = p.matcher(string);

            if (m.matches()) {
                Double value = Double.valueOf(string.replace(".", "").replace(",", "."));

                if (value.equals(0.0D)) {
                    throw new TypeConversionException("");
                }

                return value;

            } else {
                throw new TypeConversionException("");
            }

        } catch (Exception e) {

            throw new TypeConversionException("");
        }

    }

    @Override
    public String format(Object o) {
        return "";
    }

    @Override
    public Class<?> getType() {
        return Double.class;
    }

}
