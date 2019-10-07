/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

import com.core.behavior.dto.TicketDuplicityDTO;
import com.core.behavior.model.Ticket;
import java.util.List;


/**
 *
 * @author thiag
 */
public interface IValidatorDuplicity {

    void validate(List<TicketDuplicityDTO> list, Ticket ticket);

}
