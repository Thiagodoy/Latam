/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import com.core.behavior.model.Ticket;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
    
    private static final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
    private static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    
    public TicketIntegrationDTO(Ticket ticket){
        
        this.dataEmissao = Optional.ofNullable(ticket.getDataEmissao()).isPresent() ? this.formater.format(ticket.getDataEmissao()) : "";
        this.dataEmbarque = Optional.ofNullable(ticket.getDataEmbarque()).isPresent() ? this.formater.format(ticket.getDataEmbarque()) : ""; 
        this.horaEmbarque = Optional.ofNullable(ticket.getHoraEmbarque()).isPresent() ? ticket.getHoraEmbarque() : "";
        this.ciaBilhete = Optional.ofNullable(ticket.getCiaBilhete()).isPresent() ? ticket.getCiaBilhete() : "";
        this.trecho = Optional.ofNullable(ticket.getTrecho()).isPresent() ? ticket.getTrecho() : "";
        this.origem = Optional.ofNullable(ticket.getOrigem()).isPresent() ? ticket.getOrigem(): "";
        this.destino = Optional.ofNullable(ticket.getDestino()).isPresent() ? ticket.getDestino() : "";
        this.cupom = Optional.ofNullable(ticket.getCupom()).isPresent() ? String.valueOf(ticket.getCupom()) : "";
        this.bilhete = Optional.ofNullable(ticket.getBilhete()).isPresent() ? ticket.getBilhete() : "";
        this.tipo = Optional.ofNullable(ticket.getTipo()).isPresent() ? ticket.getTipo() : "";
        this.cabine = Optional.ofNullable(ticket.getCabine()).isPresent() ? ticket.getCabine() : "";
        this.ciaVoo = Optional.ofNullable(ticket.getCiaVoo()).isPresent() ? ticket.getCiaVoo() : "";
        this.valorBrl = Optional.ofNullable(ticket.getValorBrl()).isPresent() ? numberFormat.format(ticket.getValorBrl()) : "0,00";
        this.empresa = Optional.ofNullable(ticket.getEmpresa()).isPresent() ? ticket.getEmpresa() : "";
        this.cnpj = Optional.ofNullable(ticket.getCnpj()).isPresent() ? ticket.getCnpj() : "";
        this.iataAgencia = Optional.ofNullable(ticket.getIataAgencia()).isPresent() ? String.valueOf( ticket.getIataAgencia()) : "";
        this.baseVenda = Optional.ofNullable(ticket.getBaseVenda()).isPresent() ? ticket.getBaseVenda() : "";
        this.qtdPax = Optional.ofNullable(ticket.getQtdPax()).isPresent() ? String.valueOf(ticket.getQtdPax()) : "";
        this.numVoo = Optional.ofNullable(ticket.getNumVoo()).isPresent() ? String.valueOf(ticket.getNumVoo()) : "";
        this.consolidada = Optional.ofNullable(ticket.getConsolidada()).isPresent() ? ticket.getConsolidada() : "";
        
        
        
    }
    
    
}
