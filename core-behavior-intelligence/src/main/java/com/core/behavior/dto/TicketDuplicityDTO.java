package com.core.behavior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDuplicityDTO {

    private String agrupamentoA;
    private String agrupamentoB;
    private String agrupamentoC;
    private String bilheteBehavior;
    private Long cupom;

}
