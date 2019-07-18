/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class HeaderShortIntegrationDTO {

    private String headerDataEmissao = "DATA_EMISSAO";
    private String headerDataVoo = "DATA_EMBARQUE";
    private String headerHoraVoo = "HORA_EMBARQUE";
    private String headerCiaBilhete = "CIA_BILHETE";
    private String headerTrechoTkt = "TRECHO";
    private String headerAtoOrigem = "ORIGEM";
    private String headerAtoDestino = "DESTINO";
    private String headerNumeroCupom = "CUPOM";
    private String headerBilhete = "BILHETE";
    private String headerTipoVenda = "TIPO";
    private String headerClasseCabine = "CABINE";
    private String headerCiaVoo = "CIA_VOO";
    private String headerValorBrl = "VALOR_BRL";
    private String headerClienteEmpresa = "EMPRESA";
    private String headerCnpjClienteEmpresa = "CNPJ";
    private String headerIataAgenciaEmissora = "IATA_AGENCIA";
    private String headerBaseVenda = "BASE_VENDA";
    private String headerQtdePax = "QTD_PAX";
    private String headerNumVoo = "NUM_VOO";
    private String headerAgenciaConsolidada = "CONSOLIDADA";

}
