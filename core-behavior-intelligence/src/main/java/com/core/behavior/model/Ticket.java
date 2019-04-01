package com.core.behavior.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(schema = "behavior", name = "ticket")
@Data
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATA_EMISSAO", nullable = false)
    private Date dataEmissao;

    @Column(name = "DATA_VOO", nullable = false)
    private Date dataVoo;

    @Column(name = "HORA_VOO", nullable = false)
    private String horaVoo;

    @Column(name = "CIA_BILHETE", nullable = false)
    private String ciaBilhete;

    @Column(name = "TRECHO_TKT", nullable = false)
    private String trechoTkt;

    @Column(name = "ATO_ORIGEM", nullable = false)
    private String atoOrigem;

    @Column(name = "ATO_DESTINO", nullable = false)
    private String atoDestino;

    @Column(name = "NUMERO_CUPOM", nullable = false)
    private Long nroCupom;

    @Column(name = "BILHETE", nullable = false)
    private Long bilhete;

    @Column(name = "TIPO_VENDA", nullable = false)
    private String tipoVenda;

    @Column(name = "CLASSE_CABINE", nullable = false)
    private String classeCabine;

    @Column(name = "CIA_VOO", nullable = false)
    private String ciaVoo;

    @Column(name = "VALOR_BRL", nullable = false)
    private BigDecimal valorBrl;

    @Column(name = "CLIENTE_EMPRESA")
    private String clienteEmpresa;

    @Column(name = "CNPJ_CLIENTE_EMPRESA")
    private String cnpjClienteEmpresa;

    @Column(name = "IATA_AGENCIA_EMISSORA", nullable = false)
    private Long iataAgenciaEmissora;

    @Column(name = "BASE_VENDA")
    private String baseVenda;

    @Column(name = "QTDE_PAX")
    private Long qtdePax;

    @Column(name = "NUM_VOO")
    private Long numVoo;

    @Column(name = "AGENCIA_CONSOLIDADA")
    private String agenciaConsolidada;

    @Column(name = "DATA_EXTRACAO", nullable = false)
    private Date dataExtracao;
    
    @Column(name = "HORA_EMISSAO")
    private String horaEmissao;
    
    @Column(name = "DATA_RESERVA")
    private Date dataReserva;
    
    @Column(name = "HORA_RESERVA")
    private String horaReserva;
    
    @Column(name = "BASE_TARIFARIA", nullable = false)
    private String baseTarifaria;    
    
    @Column(name = "FAMILIA_TARIFARIA")
    private String familiaTarifaria;
    
    @Column(name = "CLASSE_TARIFA",nullable = false)
    private String classeTarifa;
    
    @Column(name = "CLASSE_SERVIÇO",nullable = false)
    private String classeServico;
    
    @Column(name = "OnD_DIRECIONAL")
    private String OndDirecional;
    
    @Column(name = "TOUR_CODE", nullable = false)
    private String tourCode;
    
    @Column(name = "RT_OW",nullable = false)
    private String rtOn;
    
    @Column(name = "VALOR_US$", nullable = false)
    private BigDecimal valorUs;
    
    @Column(name = "TARIFA_PUBLICA_R$", nullable = false)
    private BigDecimal tarifaPublica;   
    
    @Column(name = "TARIFA_PUBLICA_US$")
    private BigDecimal tarifaPublicUs;
    
    @Column(name = "PNR_AGENCIA")
    private String pnrAgencia;
    
    @Column(name = "PNR_CIA_AEREA")
    private String pnrCiaArea;
    
    @Column(name = "SELFBOOKING_OFFLINE")
    private String selfBookingOffiline;
    
    @Column(name = "NOME_PAX", nullable = false)
    private String nomePax;
    
    @Column(name = "TIPO_PAX", nullable = false)
    private String tipoPax;
    
    @Column(name = "CPF_PAX")
    private Long cpfPax;
    
    @Column(name = "TIER_FIDELIDADE_PAX")
    private String tierFidelidadePax;
    
    @Column(name = "TIPO_PAGAMENTO", nullable = false)
    private String tipoPagamento;
    
    @Column(name = "DIGITO_VERIFICADOR_CC")
    private Long digitoVerificadorCC;
    
    @Column(name = "NOME_CLIENTE")
    private String nomeCliente;
    
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

}
