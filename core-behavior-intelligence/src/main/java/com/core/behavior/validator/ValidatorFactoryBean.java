/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class ValidatorFactoryBean implements ValidatorFactory{

    @Autowired
    private LogService logService;
    
    @Autowired    
    private TicketService ticketService;
    
    @Override
    public IValidator getBean(ValidatorEnum type) {
        
        
        if(type.equals(ValidatorEnum.SHORT)){
            return new ValidatorShortLayout();
        }
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
