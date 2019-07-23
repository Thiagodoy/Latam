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
public class ValidatorFactoryBean implements ValidatorFactory {

    @Autowired
    private LogService logService;

    

    @Override
    public IValidator getBean() {
        return  new Validator(logService);              
    }

}
