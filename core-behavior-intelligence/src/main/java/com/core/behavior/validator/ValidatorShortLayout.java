/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.RecordErrorEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.TypeErrorEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
public class ValidatorShortLayout implements IValidatorShortLayout {

    @Autowired
    private LogService logService;

    @Autowired
    private TicketService ticketService;

    private Ticket ticket;
    private List<RecordErrorEnum> errors = new ArrayList<>();

    public ValidatorShortLayout(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public IValidatorShortLayout checkDataEmissao() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkDataVoo() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkHoraVoo() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkCiaBilhete() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkTrechoTkt() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkAtoOrigem() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkAtoDestino() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkNumeroCupom() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkBilhete() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkTipoVenda() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkClasseCabine() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkCiaVoo() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkValorBrl() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkClienteEmpresa() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkCnpjClienteEmpresa() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkIataAgenciaEmissora() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkBaseVenda() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkQtdPax() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkNumVoo() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkAgenciaConsolidada() {
        return this;
    }

    @Override
    public void validate() {
        try {

            this.checkDataEmissao().
                    checkDataVoo().
                    checkHoraVoo().
                    checkCiaBilhete().
                    checkTrechoTkt().
                    checkAtoOrigem().
                    checkAtoDestino().
                    checkNumeroCupom().
                    checkBilhete().
                    checkTipoVenda().
                    checkClasseCabine().
                    checkCiaVoo().
                    checkValorBrl().
                    checkClienteEmpresa().
                    checkCnpjClienteEmpresa().
                    checkIataAgenciaEmissora().
                    checkBaseVenda().
                    checkQtdPax().
                    checkNumVoo().
                    checkAgenciaConsolidada();

            this.errors.forEach(e -> {
                Log log = new Log();
                log.setCreatedAt(LocalDateTime.now());
                log.setFileId(this.ticket.getFileId());
                log.setMessageError(e.message);
                log.setType(TypeErrorEnum.RECORD);
                logService.saveLog(log);
            });

            TicketStatusEnum status = !errors.isEmpty() ? TicketStatusEnum.UNAPPROVED : TicketStatusEnum.APPROVED;
            this.ticket.setStatus(status);
            this.ticketService.save(ticket);

        } catch (Exception e) {
            Log log = new Log();
            log.setCreatedAt(LocalDateTime.now());
            log.setFileId(this.ticket.getFileId());
            log.setMessageError(e.getMessage());
            log.setType(TypeErrorEnum.RECORD);
            logService.saveLog(log);
        }
    }
    
}
