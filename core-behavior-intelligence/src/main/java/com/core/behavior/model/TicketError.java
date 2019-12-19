/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import com.core.behavior.annotations.PositionColumnExcel;
import com.core.behavior.annotations.PositionParameter;
import com.core.behavior.util.MessageErrors;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.util.CellAddress;

/**
 *
 * @author thiag
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "ticket_error")
public class TicketError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositionParameter(value = 1)
    @Column(name = "id")
    public Long id;

    @PositionColumnExcel(column = "B")
    @PositionParameter(value = 2)
    @Column(name = "data_emissao")
    public Long dataEmissao = 0L;

    @PositionColumnExcel(column = "C")
    @PositionParameter(value = 3)
    @Column(name = "data_embarque")
    public Long dataEmbarque = 0L;

    @PositionColumnExcel(column = "D")
    @PositionParameter(value = 4)
    @Column(name = "hora_embarque")
    public Long horaEmbarque = 0L;

    @PositionColumnExcel(column = "E")
    @PositionParameter(value = 5)
    @Column(name = "cia_bilhete")
    public Long ciaBilhete = 0L;

    @PositionColumnExcel(column = "F")
    @PositionParameter(value = 6)
    @Column(name = "trecho")
    public Long trecho = 0L;

    @PositionColumnExcel(column = "G")
    @PositionParameter(value = 7)
    @Column(name = "origem")
    public Long origem = 0L;

    @PositionColumnExcel(column = "H")
    @PositionParameter(value = 8)
    @Column(name = "destino")
    public Long destino = 0L;

    @PositionColumnExcel(column = "I")
    @PositionParameter(value = 9)
    @Column(name = "cupom")
    public Long cupom = 0L;

    @PositionColumnExcel(column = "J")
    @PositionParameter(value = 10)
    @Column(name = "bilhete")
    public Long bilhete = 0L;

    @PositionColumnExcel(column = "K")
    @PositionParameter(value = 11)
    @Column(name = "tipo")
    public Long tipo = 0L;

    @PositionColumnExcel(column = "L")
    @PositionParameter(value = 12)
    @Column(name = "cabine")
    public Long cabine = 0L;

    @PositionColumnExcel(column = "M")
    @PositionParameter(value = 13)
    @Column(name = "cia_voo")
    public Long ciaVoo = 0L;

    @PositionColumnExcel(column = "N")
    @PositionParameter(value = 14)
    @Column(name = "valor_brl")
    public Long valorBrl = 0L;

    @PositionColumnExcel(column = "O")
    @PositionParameter(value = 15)
    @Column(name = "empresa")
    public Long empresa = 0L;

    @PositionColumnExcel(column = "P")
    @PositionParameter(value = 16)
    @Column(name = "cnpj")
    public Long cnpj = 0L;

    @PositionColumnExcel(column = "Q")
    @PositionParameter(value = 17)
    @Column(name = "iata_agencia")
    public Long iataAgencia = 0L;

    @PositionColumnExcel(column = "R")
    @PositionParameter(value = 18)
    @Column(name = "base_venda")
    public Long baseVenda = 0L;

    @PositionColumnExcel(column = "S")
    @PositionParameter(value = 19)
    @Column(name = "qtd_pax")
    public Long qtdPax = 0L;

    @PositionColumnExcel(column = "T")
    @PositionParameter(value = 20)
    @Column(name = "num_voo")
    public Long numVoo = 0L;

    @PositionColumnExcel(column = "U")
    @PositionParameter(value = 21)
    @Column(name = "consolidada")
    public Long consolidada = 0L;

    @PositionColumnExcel(column = "V")
    @PositionParameter(value = 22)
    @Column(name = "data_extracao")
    public Long dataExtracao = 0L;

    @PositionColumnExcel(column = "W")
    @PositionParameter(value = 23)
    @Column(name = "hora_emissao")
    public Long horaEmissao = 0L;

    @PositionColumnExcel(column = "X")
    @PositionParameter(value = 24)
    @Column(name = "data_reserva")
    public Long dataReserva = 0L;

    @PositionColumnExcel(column = "Y")
    @PositionParameter(value = 25)
    @Column(name = "hora_reserva")
    public Long horaReserva = 0L;

    @PositionColumnExcel(column = "Z")
    @PositionParameter(value = 26)
    @Column(name = "hora_pouso")
    public Long horaPouso = 0L;

    @PositionColumnExcel(column = "AA")
    @PositionParameter(value = 27)
    @Column(name = "base_tarifaria")
    public Long baseTarifaria = 0L;

    @PositionColumnExcel(column = "AB")
    @PositionParameter(value = 28)
    @Column(name = "tkt_designator")
    public Long tktDesignator = 0L;

    @PositionColumnExcel(column = "AC")
    @PositionParameter(value = 29)
    @Column(name = "familia_tarifaria")
    public Long familiaTarifaria = 0L;

    @PositionColumnExcel(column = "AD")
    @PositionParameter(value = 30)
    @Column(name = "classe_tarifa")
    public Long classeTarifa = 0L;

    @PositionColumnExcel(column = "AE")
    @PositionParameter(value = 31)
    @Column(name = "classe_servico")
    public Long classeServico = 0L;

    @PositionColumnExcel(column = "AF")
    @PositionParameter(value = 32)
    @Column(name = "ond_direcional")
    public Long ondDirecional = 0L;

    @PositionColumnExcel(column = "AG")
    @PositionParameter(value = 33)
    @Column(name = "tour_code")
    public Long tourCode = 0L;

    @PositionColumnExcel(column = "AH")
    @PositionParameter(value = 34)
    @Column(name = "rt_ow")
    public Long rtOw = 0L;

    @PositionColumnExcel(column = "AI")
    @PositionParameter(value = 35)
    @Column(name = "valor_us")
    public Long valorUs = 0L;

    @PositionColumnExcel(column = "AJ")
    @PositionParameter(value = 36)
    @Column(name = "tarifa_publica")
    public Long tarifaPublica = 0L;

    @PositionColumnExcel(column = "AK")
    @PositionParameter(value = 37)
    @Column(name = "tarifa_public_us")
    public Long tarifaPublicUs = 0L;

    @PositionColumnExcel(column = "AL")
    @PositionParameter(value = 38)
    @Column(name = "pnr_agencia")
    public Long pnrAgencia = 0L;

    @PositionColumnExcel(column = "AM")
    @PositionParameter(value = 39)
    @Column(name = "pnr_cia_area")
    public Long pnrCiaArea = 0L;

    @PositionColumnExcel(column = "AN")
    @PositionParameter(value = 40)
    @Column(name = "self_booking_offiline")
    public Long selfBookingOffiline = 0L;

    @PositionColumnExcel(column = "AO")
    @PositionParameter(value = 41)
    @Column(name = "nome_pax")
    public Long nomePax = 0L;

    @PositionColumnExcel(column = "AP")
    @PositionParameter(value = 42)
    @Column(name = "tipo_pax")
    public Long tipoPax = 0L;

    @PositionColumnExcel(column = "AQ")
    @PositionParameter(value = 43)
    @Column(name = "cpf_pax")
    public Long cpfPax = 0L;

    @PositionColumnExcel(column = "AR")
    @PositionParameter(value = 44)
    @Column(name = "email_pax")
    public Long emailPax = 0L;

    @PositionColumnExcel(column = "AS")
    @PositionParameter(value = 45)
    @Column(name = "cell_pax")
    public Long cellPax = 0L;

    @PositionColumnExcel(column = "AT")
    @PositionParameter(value = 46)
    @Column(name = "tier_fidelidade")
    public Long tierFidelidadePax = 0L;

    @PositionColumnExcel(column = "AU")
    @PositionParameter(value = 47)
    @Column(name = "tipo_pagamento")
    public Long tipoPagamento = 0L;

    @PositionColumnExcel(column = "AV")
    @PositionParameter(value = 48)
    @Column(name = "digito_verificador_cc")
    public Long digitoVerificadorCC = 0L;

    @PositionColumnExcel(column = "AW")
    @PositionParameter(value = 49)
    @Column(name = "grupo_empresa")
    public Long grupoEmpresa = 0L;

    @PositionColumnExcel(column = "AX")
    @PositionParameter(value = 50)
    @Column(name = "grupo_consolidada")
    public Long grupoConsolidada = 0L;

    @PositionParameter(value = 51)
    @Column(name = "file_id")
    public Long fileId;

    @PositionParameter(value = 52)
    @Column(name = "line")
    public Long line;

    @PositionParameter(value = 53)
    @Column(name = "content")
    public String content;
    
    @Transient
    private static List<Field> fields;
    
    
    @Transient
    private Long recordPosition;
    
    static{
        TicketError.fields = Arrays.asList(TicketError.class.getDeclaredFields());
    }
    

    public TicketError(Long fileId, Long line, String content) {
        this.fileId = fileId;
        this.line = line;
        this.content = content;
    }

    public void activeError(String field) {

        try {
            Field property = TicketError.class.getDeclaredField(field);
            property.set(this, 1L);
        } catch (Exception e) {
            Logger.getLogger(TicketError.class.getName()).log(Level.SEVERE, "[activeError]", e);
        }

    }
    
    
    
    public boolean hasErrorByField(String field) {

        long f = TicketError.fields
                .stream()
                .filter(fs -> fs.isAnnotationPresent(PositionColumnExcel.class) && fs.getName().equals(field))
                .mapToLong(l -> {
                    try {
                        return (long)l.get(this);
                    } catch (Exception ex) {
                        return 0L;
                    }
                }).reduce(Long::sum).getAsLong();
        
        return f > 0;
    }

    public boolean hasError() {

        long f = TicketError.fields
                .stream()
                .filter(fs -> fs.isAnnotationPresent(PositionColumnExcel.class))
                .mapToLong(l -> {
                    try {
                        return (long)l.get(this);
                    } catch (Exception ex) {
                        return 0L;
                    }
                }).reduce(Long::sum).getAsLong();
        
        return f > 0;
    }

    public Optional<String> hasComments(CellAddress cellAddress) {

        String colum = cellAddress.formatAsString().replaceAll("(\\d)*", "");

        

        Optional<Field> field = TicketError.fields
                .stream()
                .filter(f -> f.isAnnotationPresent(PositionColumnExcel.class) && f.getAnnotation(PositionColumnExcel.class).column().equals(colum))
                .findFirst();

        if (field.isPresent()) {

            try {
                Long value = (long) field.get().get(this);

                if (value.equals(1L)) {
                    return Optional.of(MessageErrors.getMessages().get(field.get().getName()));
                } else {
                    return Optional.empty();
                }

            } catch (IllegalAccessException ex) {
                Logger.getLogger(TicketError.class.getName()).log(Level.SEVERE, "[hasComments]", ex);
                return Optional.empty();
            }

        }

        return Optional.empty();

    }

}
