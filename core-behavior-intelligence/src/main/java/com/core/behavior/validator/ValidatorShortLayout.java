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
import com.core.behavior.util.Utils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static final List<String> layoutMin = Arrays.asList("dataEmissao", "dataEmbarque", "horaEmbarque", "ciaBilhete", "trecho", "origem", "destino", "cupom", "bilhete", "tipo", "cabine", "ciaVoo", "valorBrl", "empresa", "cnpj", "iataAgencia", "baseVenda", "qtdPax", "numVoo", "consolidada");

    private Ticket ticket;
    private List<RecordErrorEnum> errors = new ArrayList<>();

    public ValidatorShortLayout(Ticket ticket) {
        this.ticket = ticket;
    }

    private void generateLog(Ticket t, String message, String field) {
        Log log = new Log();
        log.setCreatedAt(LocalDateTime.now());
        log.setFileId(this.ticket.getFileId());
        log.setFieldName(field);
        log.setMessageError(message);
        log.setType(TypeErrorEnum.RECORD);
        logService.saveLog(log);
    }

    @Override
    public IValidatorShortLayout checkDataEmissao() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime emissao = Utils.dateToLocalDateTime(ticket.getDataEmissao());
        LocalDateTime voo = Utils.dateToLocalDateTime(ticket.getDataEmbarque());

        //:TODO DATA RESERVA NÂO TEM NO LAYOUT PEQUENO
        if (emissao.isAfter(now) || emissao.isAfter(voo)) {
            this.generateLog(ticket, "Data da emissão é maior que a data da extratção.\nOu Data da emissão é maior que a data do embarque", "dataEmissao");
        }
        return this;
    }

    @Override
    public IValidatorShortLayout checkDataVoo() {

        LocalDateTime emissao = Utils.dateToLocalDateTime(ticket.getDataEmissao());
        LocalDateTime dataLimite = Utils.dateToLocalDateTime(ticket.getDataEmissao()).plusDays(360);
        LocalDateTime dataVoo = Utils.dateToLocalDateTime(ticket.getDataEmbarque());
        StringBuilder message = new StringBuilder();

        if (dataVoo.isAfter(dataLimite)) {
            message.append("Data embarque ultrapassa os 365 dias da data de emissão do ticket,\n");
        }

        if (dataVoo.isBefore(emissao)) {
            message.append("Data embarque menor que a data de emissão do ticket\n");
        }

        this.generateLog(ticket, message.toString(), "dataEmbarque");

        return this;
    }

    @Override
    public IValidatorShortLayout checkHoraVoo() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkCiaBilhete() {

        StringBuilder message = new StringBuilder();

        //:TODO FALTA FAZER UM CHECK NA BASE
        if (ticket.getCiaBilhete().length() > 2 || ticket.getCiaBilhete().length() < 1) {
            message.append("Incorreto o numero de carateres alfanumérico da compania aérea");
        }

        this.generateLog(ticket, message.toString(), "ciaBilhete");
        
        return this;
    }

    @Override
    public IValidatorShortLayout checkTrechoTkt() {
        
        
        
        StringBuilder message = new StringBuilder();
        
        if(ticket.getTrecho().length() < 7 ){
             message.append("Incorreto o numero de carateres alfanumérico da compania aérea");
        }
        
        
//        
//        Inserir os códigos IATA dos aeroportos de origem (ATO_ORIGEM) e destino (ATO_DESTINO);
//  Correto os códigos de aeroportos separados pelo caractere "/" barra;
//  Correto campo estar preenchido;
//  Correto mínimo de 07 caracteres, separados a cada três caracteres com apenas uma barra;
//  Correto o código IATA quando na sequencia ser diferentes;
//  Incorreto o código IATA quando na sequencia ser repetido;
//  Incorreto o campo estar em branco;
//  Incorreto o campo ter espaço, número ou caracter diferente de "/" barra;
//  Incorreto o campo iniciar com número ou barra;
        
        
        return this;
    }

    @Override
    public IValidatorShortLayout checkAtoOrigem() {
        
         StringBuilder message = new StringBuilder();
         
//         if(ticket.getOrigem().equals(ticket.getDestino())){
//             messe
//         }
        
        
        
//        
//        Inserir o código padrão IATA do aeroporto de origem do cupom;
//  Correto o campo estar preenchido;
//  Correto somente 03 caracteres, somente letras;
//  Correto código IATA "ATO_ORIGEM" ser diferente do código IATA "ATO_DESTINO";
//  
//Incorreto o campo estar em branco;
//  Incorreto quando menos ou mais que três caracteres e diferente de letras;
//  Incorreto para números;
//  Incorreto código IATA "ATO_ORIGEM" ser igual ao código IATA "ATO_DESTINO";
        
        
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
