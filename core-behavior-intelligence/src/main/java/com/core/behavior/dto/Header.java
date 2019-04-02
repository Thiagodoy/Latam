package com.core.behavior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {

    private String headerDataEmissao;
    private String headerDataVoo;
    private String headerHoraVoo;
    private String headerCiaBilhete;
    private String headerTrechoTkt;
    private String headerAtoOrigem;
    private String headerAtoDestino;
    private String headerNumeroCupom;
    private String headerBilhete;
    private String headerTipoVenda;
    private String headerClasseCabine;
    private String headerCiaVoo;
    private String headerValorBrl;
    private String headerClienteEmpresa;
    private String headerCnpjClienteEmpresa;
    private String headerIataAgenciaEmissora;
    private String headerBaseVenda;
    private String headerQtdePax;
    private String headerNumVoo;
    private String headerAgenciaConsolidada;
    private String headerDataExtracao;
    private String headerHoraEmissao;
    private String headerDataReserva;
    private String headerHoraReserva;
    private String headerBaseTarifaria;
    private String headerFamiliaTarifaria;
    private String headerClasseTarifaria;
    private String headerClasseServico;
    private String headerOndDirecional;
    private String headerTourCode;
    private String headerRtOw;
    private String hedareValorUS;
    private String headerTarifaPublicaR;
    private String headerTarifaPublicUS;
    private String headerPnAgencia;
    private String headerPnrCiaArea;
    private String headerSelfBookingOffline;
    private String headerNomePax;
    private String headerTipoPax;
    private String headerCpfPax;
    private String headerTierFidelidadePax;
    private String headerTipoPagamento;
    private String headerDigitoVerificadorCC;
    private String headerNomeCliente;
}
