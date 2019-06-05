/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

import com.core.behavior.exception.ValidationException;

/**
 *
 * @author thiag
 */
public interface IValidatorShortLayout extends IValidator{
    
    IValidatorShortLayout checkDataEmissao() ;
    IValidatorShortLayout checkDataVoo() ;
    IValidatorShortLayout checkHoraVoo()  ;
    IValidatorShortLayout checkCiaBilhete() ;
    IValidatorShortLayout checkTrechoTkt() ;
    IValidatorShortLayout checkAtoOrigem() ;
    IValidatorShortLayout checkAtoDestino() ;
    IValidatorShortLayout checkNumeroCupom() ;
    IValidatorShortLayout checkBilhete() ;
    IValidatorShortLayout checkTipoVenda() ;
    IValidatorShortLayout checkClasseCabine() ;
    IValidatorShortLayout checkCiaVoo() ;
    IValidatorShortLayout checkValorBrl() ;
    IValidatorShortLayout checkClienteEmpresa() ;
    IValidatorShortLayout checkCnpjClienteEmpresa() ;
    IValidatorShortLayout checkIataAgenciaEmissora() ;
    IValidatorShortLayout checkBaseVenda() ;
    IValidatorShortLayout checkQtdPax() ;
    IValidatorShortLayout checkNumVoo() ;
    IValidatorShortLayout checkAgenciaConsolidada() ;
}
