/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.validator;

import com.core.behavior.dto.TicketDTO;
import com.core.behavior.model.Ticket;
import java.util.Optional;

/**
 *
 * @author thiag
 */
public interface IValidator {
    
    IValidator checkDataEmissao() ;
    IValidator checkDataVoo() ;
    IValidator checkHoraVoo()  ;
    IValidator checkCiaBilhete() ;
    IValidator checkTrechoTkt() ;
    IValidator checkAtoOrigem() ;
    IValidator checkAtoDestino() ;
    IValidator checkNumeroCupom() ;
    IValidator checkBilhete() ;
    IValidator checkTipoVenda() ;
    IValidator checkClasseCabine() ;
    IValidator checkCiaVoo() ;
    IValidator checkValorBrl() ;
    IValidator checkClienteEmpresa() ;
    IValidator checkCnpjClienteEmpresa() ;
    IValidator checkIataAgenciaEmissora() ;
    IValidator checkBaseVenda() ;
    IValidator checkQtdPax() ;
    IValidator checkNumVoo() ;
    IValidator checkAgenciaConsolidada();
    
    IValidator checkDataExtracao();
    IValidator checkHoraEmissao();
    IValidator checkDataReserva();
    IValidator checkHoraReserva();
    IValidator checkHoraPouso();
    IValidator checkBaseTarifaria();
    IValidator checkTktDesignator();
    IValidator checkFamiliaTarifaria();
    IValidator checkClasseTarifa();
    IValidator checkClasseServico();
    IValidator checkOndDirecional();
    IValidator checkTourCode();
    IValidator checkRtOw();//Implementar regra
    IValidator checkValorUs();
    IValidator checkTarifaPublica();
    IValidator checkTarifaPublicaUs();
    IValidator checkPnrAgencia();
    IValidator checkPnrCiaArea();
    IValidator checkSelfBookingOffiline();
    IValidator checkNomePax();
    IValidator checkTipoPax();
    IValidator checkCpfPax();
    IValidator checkEmailPax();
    IValidator checkCellPax();
    IValidator checkTierFidelidadePax();
    IValidator checkTipoPagamento();
    IValidator checkDigitoVerificador();
    IValidator checkGrupoEmpresa();
    IValidator checkGrupoConsolida(); 
    
    void validate(TicketDTO ticketDto);   
    
    
}
