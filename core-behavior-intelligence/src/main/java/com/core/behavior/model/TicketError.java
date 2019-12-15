/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import com.core.behavior.annotations.PositionParameter;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Data
@Table(name = "ticket_error")
public class TicketError {

    @PositionParameter(value = 1)
    @Column(name = "id")
    private Long id;

    @PositionParameter(value = 2)
    @Column(name = "data_emissao")
    private Long dataEmissao = 0L;

    @PositionParameter(value = 3)
    @Column(name = "data_embarque")
    private Long dataEmbarque = 0L;

    @PositionParameter(value = 4)
    @Column(name = "hora_embarque")
    private Long horaEmbarque = 0L;

    @PositionParameter(value = 5)
    @Column(name = "cia_bilhete")
    private Long ciaBilhete = 0L;

    @PositionParameter(value = 6)
    @Column(name = "trecho")
    private Long trecho = 0L;

    @PositionParameter(value = 7)
    @Column(name = "origem")
    private Long origem = 0L;

    @PositionParameter(value = 8)
    @Column(name = "destino")
    private Long destino = 0L;

    @PositionParameter(value = 9)
    @Column(name = "cupom")
    private Long cupom = 0L;

    @PositionParameter(value = 10)
    @Column(name = "bilhete")
    private Long bilhete = 0L;

    @PositionParameter(value = 11)
    @Column(name = "tipo")
    private Long tipo = 0L;

    @PositionParameter(value = 12)
    @Column(name = "cabine")
    private Long cabine = 0L;

    @PositionParameter(value = 13)
    @Column(name = "cia_voo")
    private Long ciaVoo = 0L;

    @PositionParameter(value = 14)
    @Column(name = "valor_brl")
    private Long valorBrl = 0L;

    @PositionParameter(value = 15)
    @Column(name = "empresa")
    private Long empresa = 0L;

    @PositionParameter(value = 16)
    @Column(name = "cnpj")
    private Long cnpj = 0L;

    @PositionParameter(value = 17)
    @Column(name = "iata_agencia")
    private Long iataAgencia = 0L;

    @PositionParameter(value = 18)
    @Column(name = "base_venda")
    private Long baseVenda = 0L;

    @PositionParameter(value = 19)
    @Column(name = "qtd_pax")
    private Long qtdPax = 0L;

    @PositionParameter(value = 20)
    @Column(name = "num_voo")
    private Long numVoo = 0L;

    @PositionParameter(value = 21)
    @Column(name = "consolidada")
    private Long consolidada = 0L;

    @PositionParameter(value = 22)
    @Column(name = "data_extracao")
    private Long dataExtracao = 0L;

    @PositionParameter(value = 23)
    @Column(name = "hora_emissao")
    private Long horaEmissao = 0L;

    @PositionParameter(value = 24)
    @Column(name = "data_reserva")
    private Long dataReserva = 0L;

    @PositionParameter(value = 25)
    @Column(name = "hora_reserva")
    private Long horaReserva = 0L;

    @PositionParameter(value = 26)
    @Column(name = "hora_pouso")
    private Long horaPouso = 0L;

    @PositionParameter(value = 27 )
    @Column(name = "base_tarifaria")
    private Long baseTarifaria = 0L;

    @PositionParameter(value = 28)
    @Column(name = "tkt_designator")
    private Long tktDesignator = 0L;

    @PositionParameter(value = 29)
    @Column(name = "familia_tarifaria")
    private Long familiaTarifaria = 0L;

    @PositionParameter(value = 30)
    @Column(name = "classe_tarifa")
    private Long classeTarifa = 0L;

    @PositionParameter(value = 31)
    @Column(name = "classe_servico")
    private Long classeServico = 0L;

    @PositionParameter(value = 32 )
    @Column(name = "ond_direcional")
    private Long ondDirecional = 0L;

    @PositionParameter(value = 33)
    @Column(name = "tour_code")
    private Long tourCode = 0L;

    @PositionParameter(value = 34)    
    @Column(name = "rt_ow")
    private Long rtOw = 0L;

    @PositionParameter(value = 35)
    @Column(name = "valor_us")
    private Long valorUs = 0L;

    @PositionParameter(value = 36)
    @Column(name = "tarifa_publica")
    private Long tarifaPublica = 0L;

    @PositionParameter(value = 37)
    @Column(name = "tarifa_public_us")
    private Long tarifaPublicUs = 0L;

    @PositionParameter(value = 38)
    @Column(name = "pnr_agencia")
    private Long pnrAgencia = 0L;

    @PositionParameter(value = 39)
    @Column(name = "pnr_cia_area")
    private Long pnrCiaArea = 0L;

    @PositionParameter(value = 40)
    @Column(name = "self_booking_offiline")
    private Long selfBookingOffiline = 0L;

    @PositionParameter(value = 41)
    @Column(name = "nome_pax")
    private Long nomePax = 0L;

    @PositionParameter(value = 42)
    @Column(name = "tipo_pax")
    private Long tipoPax = 0L;

    @PositionParameter(value = 43)
    @Column(name = "cpf_pax")
    private Long cpfPax = 0L;

    @PositionParameter(value = 44)
    @Column(name = "email_pax")
    private Long emailPax = 0L;

    @PositionParameter(value = 45)
    @Column(name = "cell_pax")
    private Long cellPax = 0L;

    @PositionParameter(value = 46)
    @Column(name = "tier_fidelidade")
    private Long tierFidelidadePax = 0L;

    @PositionParameter(value = 47)
    @Column(name = "tipo_pagamento")
    private Long tipoPagamento = 0L;

    @PositionParameter(value = 48)
    @Column(name = "digito_verificador_cc")
    private Long digitoVerificadorCC = 0L;

    @PositionParameter(value = 49)
    @Column(name = "grupo_empresa")
    private Long grupoEmpresa = 0L;

    @PositionParameter(value = 50)
    @Column(name = "grupo_consolidada")
    private Long grupoConsolidada = 0L;

    @PositionParameter(value = 51)
    @Column(name = "file_id")
    private Long fileId;

    @PositionParameter(value = 52)
    @Column(name = "line")
    private Long line;

    @PositionParameter(value = 53)
    @Column(name = "content")
    private String content;

    public TicketError(Long fileId, Long line, String content) {
        this.fileId = fileId;
        this.line = line;
        this.content = content;        
    }

    public void activeError(String field) {

        try {
            Field property = TicketError.class.getDeclaredField(field);
            property.setLong(this, 1L);
        } catch (Exception e) {
            Logger.getLogger(TicketError.class.getName()).log(Level.SEVERE, "[activeError]", e);
        }

    }

    public boolean hasError(){

        boolean result = false;
        Field[] f = TicketError.class.getDeclaredFields();

        for (Field field : f) {
            Long value;
            try {
                value = field.getLong(this);
                if (value > 0) {
                    return true;
                }
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TicketError.class.getName()).log(Level.SEVERE, "[hasError]", ex);
                return false;
            }

        }

        return result;
    }
    
    

}
