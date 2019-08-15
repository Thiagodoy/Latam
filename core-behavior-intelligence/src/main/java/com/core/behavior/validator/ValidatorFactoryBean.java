/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class ValidatorFactoryBean implements ValidatorFactory {

    

    @Override
    public IValidator getBean() {
        return  new Validator();              
    }

}
