/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

/**
 *
 * @author thiag
 */
public interface ValidatorFactory{

    IValidator getBean(ValidatorEnum type);
    
}