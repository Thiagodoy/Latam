/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.reader;

import java.util.Optional;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class DoubleHandler implements TypeHandler{

    @Override
    public Object parse(String string) throws TypeConversionException {
        
        return Optional.of(string).isPresent() ? Double.valueOf(string.replace(",", "").replace(".", "")) / 100 : Double.valueOf("0.0");
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
