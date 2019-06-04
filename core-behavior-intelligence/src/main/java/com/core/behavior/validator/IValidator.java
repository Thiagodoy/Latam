/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

import com.core.behavior.exception.ValidationException;
import com.core.behavior.model.Ticket;

/**
 *
 * @author thiag
 */
public interface IValidator {
    void validate(Ticket ticket) throws ValidationException;
}
