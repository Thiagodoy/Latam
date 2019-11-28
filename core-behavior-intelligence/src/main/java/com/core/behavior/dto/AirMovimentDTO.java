/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import com.core.behavior.annotations.PositionParameter;
import com.core.behavior.model.Ticket;
import com.core.behavior.util.TicketLayoutEnum;
import java.util.Date;
import java.util.Optional;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class AirMovimentDTO {

    @PositionParameter(value = 0)
    public Long id;

    @PositionParameter(value = 1)
    public Date dataEmissao;

    @PositionParameter(value = 2)
    public Date dataEmbarque;

    @PositionParameter(value = 3)
    public String horaEmbarque;

    @PositionParameter(value = 4)
    public String ciaBilhete;

    @PositionParameter(value = 5)
    public String trecho;

    @PositionParameter(value = 6)
    public String origem;

    @PositionParameter(value = 7)
    public String destino;

    @PositionParameter(value = 8)
    public Long cupom;

    @PositionParameter(value = 9)
    public String bilhete;

    @PositionParameter(value = 10)
    public String tipo;

    @PositionParameter(value = 11)
    public String cabine;

    @PositionParameter(value = 12)
    public String ciaVoo;

    @PositionParameter(value = 13)
    public Double valorBrl;

    @PositionParameter(value = 14)
    public String empresa;

    @PositionParameter(value = 15)
    public String cnpj;

    @PositionParameter(value = 16)
    public Long iataAgencia;

    @PositionParameter(value = 17)
    public String baseVenda;

    @PositionParameter(value = 18)
    public Long qtdPax;

    @PositionParameter(value = 19)
    public String numVoo;

    @PositionParameter(value = 20)
    public String consolidada;

    @PositionParameter(value = 21)
    public Date dataExtracao;

    @PositionParameter(value = 22)
    public String horaEmissao;

    @PositionParameter(value = 23)
    public Date dataReserva;

    @PositionParameter(value = 24)
    public String horaReserva;

    @PositionParameter(value = 25)
    public String horaPouso;

    @PositionParameter(value = 26)
    public String baseTarifaria;

    @PositionParameter(value = 27)
    public String tktDesignator;

    @PositionParameter(value = 28)
    public String familiaTarifaria;

    @PositionParameter(value = 29)
    public String classeTarifa;

    @PositionParameter(value = 30)
    public String classeServico;

    @PositionParameter(value = 31)
    public String ondDirecional;

    @PositionParameter(value = 32)
    public String tourCode;

    @PositionParameter(value = 33)
    public String rtOw;

    @PositionParameter(value = 34)
    public Double valorUs;

    @PositionParameter(value = 35)
    public Double tarifaPublica;

    @PositionParameter(value = 36)
    public Double tarifaPublicUs;

    @PositionParameter(value = 37)
    public String pnrAgencia;

    @PositionParameter(value = 38)
    public String pnrCiaArea;

    @PositionParameter(value = 39)
    public String selfBookingOffiline;

    @PositionParameter(value = 40)
    public String nomePax;

    @PositionParameter(value = 41)
    public String tipoPax;

    @PositionParameter(value = 42)
    public String cpfPax;

    @PositionParameter(value = 43)
    public String emailPax;

    @PositionParameter(value = 44)
    public String cellPax;

    @PositionParameter(value = 45)
    public String tierFidelidadePax;

    @PositionParameter(value = 46)
    public String tipoPagamento;

    @PositionParameter(value = 47)
    public Long digitoVerificadorCC;

    @PositionParameter(value = 48)
    public String grupoEmpresa;

    @PositionParameter(value = 49)
    public String grupoConsolidada;

    @PositionParameter(value = 50)
    public String nomeCliente;

    @PositionParameter(value = 51)
    public Long status;

    public AirMovimentDTO(Ticket ticket) {
        this.status = 0L;

        this.dataEmissao = ticket.getDataEmissao();
        this.dataEmbarque = ticket.getDataEmbarque();
        this.horaEmbarque = Optional.ofNullable(ticket.getHoraEmbarque()).isPresent() ? ticket.getHoraEmbarque() : "";
        this.ciaBilhete = Optional.ofNullable(ticket.getCiaBilhete()).isPresent() ? ticket.getCiaBilhete() : "";
        this.trecho = Optional.ofNullable(ticket.getTrecho()).isPresent() ? ticket.getTrecho() : "";
        this.origem = Optional.ofNullable(ticket.getOrigem()).isPresent() ? ticket.getOrigem() : "";
        this.destino = Optional.ofNullable(ticket.getDestino()).isPresent() ? ticket.getDestino() : "";
        this.cupom = ticket.getCupom();
        this.bilhete = Optional.ofNullable(ticket.getBilheteBehavior()).isPresent() ? ticket.getBilheteBehavior() : "";
        this.tipo = Optional.ofNullable(ticket.getTipo()).isPresent() ? ticket.getTipo() : "";
        this.cabine = Optional.ofNullable(ticket.getCabine()).isPresent() ? ticket.getCabine() : "";
        this.ciaVoo = Optional.ofNullable(ticket.getCiaVoo()).isPresent() ? ticket.getCiaVoo() : "";
        this.valorBrl = ticket.getValorBrl();
        this.empresa = Optional.ofNullable(ticket.getEmpresa()).isPresent() ? ticket.getEmpresa() : "";
        this.cnpj = Optional.ofNullable(ticket.getCnpj()).isPresent() ? ticket.getCnpj() : "";
        this.iataAgencia = ticket.getIataAgencia();
        this.baseVenda = Optional.ofNullable(ticket.getBaseVenda()).isPresent() ? ticket.getBaseVenda() : "";
        this.qtdPax = ticket.getQtdPax();
        this.numVoo = Optional.ofNullable(ticket.getNumVoo()).isPresent() ? String.valueOf(ticket.getNumVoo()) : "";
        this.consolidada = Optional.ofNullable(ticket.getConsolidada()).isPresent() ? ticket.getConsolidada() : "";

        if (ticket.getLayout().equals(TicketLayoutEnum.FULL)) {
            this.dataExtracao = ticket.getDataExtracao();
            this.horaEmissao = ticket.getHoraEmissao();
            this.dataReserva = ticket.getDataReserva();
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
            this.valorUs = ticket.getValorUs();
            this.tarifaPublica = ticket.getTarifaPublica();
            this.tarifaPublicUs = ticket.getTarifaPublicUs();
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
            this.digitoVerificadorCC = ticket.getDigitoVerificadorCC();
            this.grupoEmpresa = Optional.ofNullable(ticket.getGrupoEmpresa()).isPresent() ? ticket.getGrupoEmpresa() : "";
            this.nomeCliente = ticket.getNomeCliente();
            this.grupoConsolidada = Optional.ofNullable(ticket.getGrupoConsolidada()).isPresent() ? ticket.getGrupoConsolidada() : "";

        }

    }
}
