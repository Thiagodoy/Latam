/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

import com.core.behavior.exception.ValidationException;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.LogService;
import com.core.behavior.util.RecordErrorEnum;
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
    public IValidatorShortLayout checkDataVoo(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkHoraVoo(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkCiaBilhete(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkTrechoTkt(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkAtoOrigem(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkAtoDestino(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkNumeroCupom(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkBilhete(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkTipoVenda(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkClasseCabine(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkCiaVoo(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkValorBrl(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkClienteEmpresa(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkCnpjClienteEmpresa(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkIataAgenciaEmissora(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkBaseVenda() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkQtdPax(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkNumVoo(){
        return this;
    }

    @Override
    public IValidatorShortLayout checkAgenciaConsolidada(){
        return this;
    }

    @Override
    public void validate(Ticket ticket)  throws ValidationException {
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
            
          if(!errors.isEmpty()){
              
          }  
            

        } catch (Exception e) {
           throw new  ValidationException();        
        }
    }

}
