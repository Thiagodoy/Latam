/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import com.core.behavior.model.Ticket;
import com.core.behavior.util.TicketLayoutEnum;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class TicketIntegrationDTO {

    private String dataEmissao;
    private String dataEmbarque;
    private String horaEmbarque;
    private String ciaBilhete;
    private String trecho;
    private String origem;
    private String destino;
    private String cupom;
    private String bilhete;
    private String tipo;
    private String cabine;
    private String ciaVoo;
    private String valorBrl;
    private String empresa;
    private String cnpj;
    private String iataAgencia;
    private String baseVenda;
    private String qtdPax;
    private String numVoo;
    private String consolidada;
    private String dataExtracao;
    private String horaEmissao;
    private String dataReserva;
    private String horaReserva;
    private String horaPouso;
    private String baseTarifaria;
    private String tktDesignator;
    private String familiaTarifaria;
    private String classeTarifa;
    private String classeServico;
    private String ondDirecional;
    private String tourCode;
    private String rtOw;
    private String valorUs;
    private String tarifaPublica;
    private String tarifaPublicUs;
    private String pnrAgencia;
    private String pnrCiaArea;
    private String selfBookingOffiline;
    private String nomePax;
    private String tipoPax;
    private String cpfPax;
    private String emailPax;
    private String cellPax;
    private String tierFidelidadePax;
    private String tipoPagamento;
    private String digitoVerificadorCC;
    private String grupoEmpresa;
    private String nomeCliente;
    private String grupoConsolidada;

    private static final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
    private static  final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    
    private synchronized static String  formatDate(Date date){
        return formater.format(date);
    }
    
    private synchronized static String formatNumber(Double value){
        return numberFormat.format(value).replaceAll("([\\sR$.])", "");
    }
    
    
    public TicketIntegrationDTO(Ticket ticket) {        

            this.dataEmissao = Optional.ofNullable(ticket.getDataEmissao()).isPresent() ? formatDate(ticket.getDataEmissao()) : "";
            this.dataEmbarque = Optional.ofNullable(ticket.getDataEmbarque()).isPresent() ? formatDate(ticket.getDataEmbarque()) : "";
            this.horaEmbarque = Optional.ofNullable(ticket.getHoraEmbarque()).isPresent() ? ticket.getHoraEmbarque() : "";
            this.ciaBilhete = Optional.ofNullable(ticket.getCiaBilhete()).isPresent() ? ticket.getCiaBilhete() : "";
            this.trecho = Optional.ofNullable(ticket.getTrecho()).isPresent() ? ticket.getTrecho() : "";
            this.origem = Optional.ofNullable(ticket.getOrigem()).isPresent() ? ticket.getOrigem() : "";
            this.destino = Optional.ofNullable(ticket.getDestino()).isPresent() ? ticket.getDestino() : "";
            this.cupom = Optional.ofNullable(ticket.getCupom()).isPresent() ? String.valueOf(ticket.getCupom()) : "";
            this.bilhete = Optional.ofNullable(ticket.getBilheteBehavior()).isPresent() ? ticket.getBilheteBehavior() : "";
            this.tipo = Optional.ofNullable(ticket.getTipo()).isPresent() ? ticket.getTipo() : "";
            this.cabine = Optional.ofNullable(ticket.getCabine()).isPresent() ? ticket.getCabine() : "";
            this.ciaVoo = Optional.ofNullable(ticket.getCiaVoo()).isPresent() ? ticket.getCiaVoo() : "";
            this.valorBrl = Optional.ofNullable(ticket.getValorBrl()).isPresent() ? formatNumber(ticket.getValorBrl()).replaceAll("([\\sR$.])", "") : "0,00";
            this.empresa = Optional.ofNullable(ticket.getEmpresa()).isPresent() ? ticket.getEmpresa() : "";
            this.cnpj = Optional.ofNullable(ticket.getCnpj()).isPresent() ? ticket.getCnpj() : "";
            this.iataAgencia = Optional.ofNullable(ticket.getIataAgencia()).isPresent() ? String.valueOf(ticket.getIataAgencia()) : "";
            this.baseVenda = Optional.ofNullable(ticket.getBaseVenda()).isPresent() ? ticket.getBaseVenda() : "";
            this.qtdPax = Optional.ofNullable(ticket.getQtdPax()).isPresent() ? String.valueOf(ticket.getQtdPax()) : "";
            this.numVoo = Optional.ofNullable(ticket.getNumVoo()).isPresent() ? String.valueOf(ticket.getNumVoo()) : "";
            this.consolidada = Optional.ofNullable(ticket.getConsolidada()).isPresent() ? ticket.getConsolidada() : "";

            if (ticket.getLayout().equals(TicketLayoutEnum.FULL)) {
                this.dataExtracao = Optional.ofNullable(ticket.getDataExtracao()).isPresent() ? formatDate(ticket.getDataExtracao()) : "";
                this.horaEmissao = Optional.ofNullable(ticket.getHoraEmissao()).isPresent() ? ticket.getHoraEmissao() : "";
                this.dataReserva = Optional.ofNullable(ticket.getDataReserva()).isPresent() ? formatDate(ticket.getDataReserva()) : "";
                this.horaReserva = Optional.ofNullable(ticket.getHoraReserva()).isPresent() ? ticket.getHoraReserva() : "";
                this.horaPouso = Optional.ofNullable(ticket.getHoraPouso()).isPresent() ? ticket.getHoraPouso() : "";
                this.baseTarifaria = Optional.ofNullable(ticket.getBaseTarifaria()).isPresent() ? ticket.getBaseTarifaria() : "";
                this.tktDesignator = Optional.ofNullable(ticket.getTktDesignator()).isPresent() ? ticket.getTktDesignator() : "";
                this.familiaTarifaria = Optional.ofNullable(ticket.getFamiliaTarifaria()).isPresent() ? ticket.getFamiliaTarifaria() : "";
                this.classeTarifa = Optional.ofNullable(ticket.getClasseTarifa()).isPresent() ? ticket.getClasseTarifa() : "";
                this.classeServico = Optional.ofNullable(ticket.getClasseServico()).isPresent() ? ticket.getClasseServico() : "";
                this.ondDirecional = Optional.ofNullable(ticket.getOndDirecional()).isPresent() ? ticket.getOndDirecional() : "";
                this.tourCode = Optional.ofNullable(ticket.getTourCode()).isPresent() ? ticket.getTourCode() : "";
                this.rtOw = Optional.ofNullable(ticket.getRtOw()).isPresent() ? ticket.getRtOw() : "";
                this.valorUs = Optional.ofNullable(ticket.getValorUs()).isPresent() ? formatNumber(ticket.getValorUs()).replaceAll("([\\sR$.])", "") : "0,00";
                this.tarifaPublica = Optional.ofNullable(ticket.getTarifaPublica()).isPresent() ? formatNumber(ticket.getTarifaPublica()).replaceAll("([\\sR$.])", "") : "0,00";
                this.tarifaPublicUs = Optional.ofNullable(ticket.getTarifaPublicUs()).isPresent() ? formatNumber(ticket.getTarifaPublicUs()).replaceAll("([\\sR$.])", "") : "0,00";
                this.pnrAgencia = Optional.ofNullable(ticket.getPnrAgencia()).isPresent() ? ticket.getPnrAgencia() : "";
                this.pnrCiaArea = Optional.ofNullable(ticket.getPnrCiaArea()).isPresent() ? ticket.getPnrCiaArea() : "";
                this.selfBookingOffiline = Optional.ofNullable(ticket.getSelfBookingOffiline()).isPresent() ? ticket.getSelfBookingOffiline() : "";
                this.nomePax = Optional.ofNullable(ticket.getNomePax()).isPresent() ? ticket.getNomePax() : "";
                this.tipoPax = Optional.ofNullable(ticket.getTipoPax()).isPresent() ? ticket.getTipoPax() : "";
                this.cpfPax = Optional.ofNullable(ticket.getCpfPax()).isPresent() ? String.valueOf(ticket.getCpfPax()) : "";
                this.emailPax = Optional.ofNullable(ticket.getEmailPax()).isPresent() ? ticket.getEmailPax() : "";
                this.cellPax = Optional.ofNullable(ticket.getCellPax()).isPresent() ? ticket.getCellPax() : "";
                this.tierFidelidadePax = Optional.ofNullable(ticket.getTierFidelidadePax()).isPresent() ? ticket.getTierFidelidadePax() : "";
                this.tipoPagamento = Optional.ofNullable(ticket.getTipoPagamento()).isPresent() ? ticket.getTipoPagamento() : "";
                this.digitoVerificadorCC = Optional.ofNullable(ticket.getDigitoVerificadorCC()).isPresent() ? String.valueOf(ticket.getDigitoVerificadorCC()) : "";
                this.grupoEmpresa = Optional.ofNullable(ticket.getGrupoEmpresa()).isPresent() ? ticket.getGrupoEmpresa() : "";
                this.nomeCliente = ticket.getNomeCliente();
                this.grupoConsolidada = Optional.ofNullable(ticket.getGrupoConsolidada()).isPresent() ? ticket.getGrupoConsolidada() : "";

            }

       
    }

}
