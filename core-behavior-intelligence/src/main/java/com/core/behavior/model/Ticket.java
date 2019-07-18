package com.core.behavior.model;

import com.core.behavior.annotations.PositionParameter;
import com.core.behavior.dto.TicketDuplicityDTO;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.Utils;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@SqlResultSetMapping(name = "TicketDuplicity",
        classes = @ConstructorResult(
                targetClass = TicketDuplicityDTO.class,
                columns = {
                    @ColumnResult(name = "field_name",  type = String.class),                    
                    @ColumnResult(name = "qtd_erro", type = Long.class),
                    @ColumnResult(name = "percentual_erro", type = Double.class),                    
                    @ColumnResult(name = "percentual_acerto", type = Double.class),                    
                    @ColumnResult(name = "qtd_total_lines", type = Long.class),
                }))

@NamedNativeQuery(name = "Ticket.listDuplicityByFileId", resultSetMapping = "TicketDuplicity",
        query = "select field_name,\n" +
" count(1) as qtd_erro,\n" +
" (truncate((count(1)/b.qtd_total_lines)*100,2)) as percentual_erro,\n" +
" (100 - (truncate((count(1)/b.qtd_total_lines)*100,2))) as percentual_acerto, \n" +
" b.qtd_total_lines \n" +
" from behavior.log a \n" +
" left join behavior.file b on a.file_id = b.id   \n" +
" where file_id = :fileId \n" +
" group by field_name, b.qtd_total_lines")

@Entity
@Table(schema = "behavior", name = "ticket")
//@IdClass(Ticket.IdClass.class)
@Data
public class Ticket {


    @PositionParameter(value = 0)
    @Column(name = "OnD_DIRECIONAL")
    public String OndDirecional;
    
    @PositionParameter(value = 1)
    @Column(name = "CONSOLIDADA")
    public String consolidada;
    
    @PositionParameter(value = 2)
    @Column(name = "DESTINO")
    public String destino;
    
    @PositionParameter(value = 3)
    @Column(name = "ORIGEM")
    public String origem;
    
    @PositionParameter(value = 4)
    @Column(name = "BASE_TARIFARIA")
    public String baseTarifaria;
    
    @PositionParameter(value = 5)
    @Column(name = "BASE_VENDA")
    public String baseVenda;
    
    @PositionParameter(value = 6)  
    @Column(name = "BILHETE")
    public String bilhete;
    
    @PositionParameter(value = 7)
    @Column(name = "CIA_BILHETE")
    public String ciaBilhete;
    
    @PositionParameter(value = 8)
    @Column(name = "CIA_VOO")
    public String ciaVoo;
    @PositionParameter(value = 9)
    @Column(name = "CABINE")
    public String cabine;
    
    @PositionParameter(value = 10)
    @Column(name = "CLASSE_SERVICO")
    public String classeServico;

    @PositionParameter(value = 11)
    @Column(name = "CLASSE_TARIFA")
    public String classeTarifa;

    @PositionParameter(value = 12)
    @Column(name = "EMPRESA")
    public String empresa;

    @PositionParameter(value = 13)
    @Column(name = "CNPJ")
    public String cnpj;
    
    @PositionParameter(value = 14)
    @Column(name = "CPF_PAX")
    public Long cpfPax;
    
    @PositionParameter(value = 15)
    @Column(name = "DATA_EMISSAO")
    public Date dataEmissao;

    @PositionParameter(value = 16)
    @Column(name = "DATA_EXTRACAO")
    public Date dataExtracao;

    @PositionParameter(value = 17)
    @Column(name = "DATA_RESERVA")
    public Date dataReserva;
    
    @PositionParameter(value = 18)   
    @Column(name = "DATA_EMBARQUE")    
    public Date dataEmbarque;
    
    @PositionParameter(value = 19)
    @Column(name = "DIGITO_VERIFICADOR_CC")
    public Long digitoVerificadorCC;
    
    @PositionParameter(value = 20)
    @Column(name = "FAMILIA_TARIFARIA")
    public String familiaTarifaria;
    
    @PositionParameter(value = 21)
    @Column(name = "HORA_EMISSAO")
    public String horaEmissao;

    @PositionParameter(value = 22)
    @Column(name = "HORA_RESERVA")
    public String horaReserva;
    
    @PositionParameter(value = 23)
    @Column(name = "HORA_EMBARQUE")
    public String horaEmbarque;    

    @PositionParameter(value = 24)
    @Column(name = "IATA_AGENCIA")
    public Long iataAgencia;

    @PositionParameter(value = 25)
    @Column(name = "NOME_CLIENTE")
    public String nomeCliente;

    @PositionParameter(value = 26)
    @Column(name = "NOME_PAX")
    public String nomePax;

    @PositionParameter(value = 27)
    @Column(name = "CUPOM")
    public Long cupom;

    @PositionParameter(value = 28)
    @Column(name = "NUM_VOO")
    public Long numVoo;
    
    @PositionParameter(value = 29)
    @Column(name = "PNR_AGENCIA")
    public String pnrAgencia;

    @PositionParameter(value = 30)
    @Column(name = "PNR_CIA_AEREA")
    public String pnrCiaArea;

    @PositionParameter(value = 31)
    @Column(name = "QTD_PAX")
    public Long qtdPax;    

    @PositionParameter(value = 32)
    @Column(name = "RT_OW")
    public String rtOn;
    
    @PositionParameter(value = 33)
    @Column(name = "SELFBOOKING_OFFLINE")
    public String selfBookingOffiline;
    
    @PositionParameter(value = 34)
    @Column(name = "TARIFA_PUBLICA_US$")
    public Double tarifaPublicUs;
    
    @PositionParameter(value = 35)
    @Column(name = "TARIFA_PUBLICA_R$")
    public Double tarifaPublica;
    
    @PositionParameter(value = 36)
    @Column(name = "TIER_FIDELIDADE_PAX")
    public String tierFidelidadePax;

    @PositionParameter(value = 37)
    @Column(name = "TIPO_PAGAMENTO")
    public String tipoPagamento;

    @PositionParameter(value = 38)
    @Column(name = "TIPO_PAX")
    public String tipoPax;

    @PositionParameter(value = 39)
    @Column(name = "TIPO")
    public String tipo;

    @PositionParameter(value = 40)
    @Column(name = "TOUR_CODE")
    public String tourCode;    

    @PositionParameter(value = 41)
    @Column(name = "TRECHO")
    public String trecho;

    @PositionParameter(value = 42)
    @Column(name = "VALOR_BRL")
    public Double valorBrl;
    
    @PositionParameter(value = 43)
    @Column(name = "VALOR_US$")
    public Double valorUs;

    @PositionParameter(value = 44)
    @Column(name = "CREATED_AT")
    public LocalDateTime createdAt;

    @PositionParameter(value = 45)
    @Column(name = "FILE_ID")
    public Long fileId;
    
    @PositionParameter(value = 46)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Long id;
    
    @Column(name = "STATUS")
    @PositionParameter(value = 47)
    @Enumerated(EnumType.STRING)
    public TicketStatusEnum status;
    
    @PositionParameter(value = 48)
    @Column(name = "LINE_FILE")
    public Long lineFile;
    
    @PositionParameter(value = 49)
    @Enumerated(EnumType.STRING)
    @Column(name = "LAYOUT")
    public TicketLayoutEnum layout;

    
    public Ticket() {
        this.createdAt = LocalDateTime.now();
    }
    
     @Data
    public static class IdClass implements Serializable {
        public String bilhete;
        public Date dataEmbarque;
    }

     @Override
     public String toString(){         
         
         return MessageFormat.format("{0};{1};{2};{3};{4};{5};{6};{7};{8};{9};{10};{11};{12};{13};{14};{15};{16};{17};{18};{19};{20}",
                 this.lineFile,
                 Utils.formatDate(this.dataEmissao),
                 Utils.formatDate(this.dataEmbarque),
                 this.horaEmbarque,
                 this.ciaBilhete,
                 this.trecho,
                 this.origem,
                 this.destino,
                 this.cupom,
                 this.bilhete,
                 this.tipo,
                 this.cabine,
                 this.ciaVoo,
                 this.valorBrl,
                 this.empresa,
                 this.cnpj,
                 this.iataAgencia,
                 this.baseVenda,
                 this.qtdPax,
                 this.numVoo,
                 this.consolidada);
     }
}
