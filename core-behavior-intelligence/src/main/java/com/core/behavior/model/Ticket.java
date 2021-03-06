package com.core.behavior.model;

import com.core.behavior.annotations.PositionParameter;
import com.core.behavior.dto.TicketValidationDTO;
import static com.core.behavior.jobs.ProcessFileJob.SIZE_BILHETE_BEHAVIOR;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.TicketTypeEnum;
import com.core.behavior.util.Utils;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */


@Entity
@Table(schema = "behavior", name = "ticket")
@DynamicUpdate
@Data
@EqualsAndHashCode(of = {"agrupamentoA", "agrupamentoC"})
public class Ticket {

    @PositionParameter(value = 0)
    @Column(name = "OnD_DIRECIONAL")
    public String ondDirecional;

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
    public String cpfPax;

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
    public String numVoo;

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
    public String rtOw;

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
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @PositionParameter(value = 50)
    @Column(name = "HORA_POUSO")
    public String horaPouso;

    @PositionParameter(value = 51)
    @Column(name = "TKT_DESIGNATOR")
    public String tktDesignator;

    @PositionParameter(value = 52)
    @Column(name = "EMAIL_PAX")
    public String emailPax;

    @PositionParameter(value = 53)
    @Column(name = "CELL_PAX")
    public String cellPax;

    @PositionParameter(value = 54)
    @Column(name = "GRUPO_EMPRESA")
    public String grupoEmpresa;

    @PositionParameter(value = 55)
    @Column(name = "GRUPO_CONSOLIDADA")
    public String grupoConsolidada;

    @PositionParameter(value = 56)
    @Column(name = "FILE_INTEGRATION")
    public String fileIntegration;

    @PositionParameter(value = 57)
    @Column(name = "CODE_AGENCY")
    public String codeAgencia;

    @PositionParameter(value = 58)
    @Column(name = "AGRUPAMENTO_A")
    public String agrupamentoA;

    @PositionParameter(value = 59)
    @Column(name = "AGRUPAMENTO_B")
    public String agrupamentoB;

    @PositionParameter(value = 60)
    @Column(name = "AGRUPAMENTO_C")
    public String agrupamentoC;

    @PositionParameter(value = 61)
    @Column(name = "BILHETE_BEHAVIOR")
    public String bilheteBehavior;

    @PositionParameter(value = 62)
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    public TicketTypeEnum type;

    @PositionParameter(value = 63)
    @Column(name = "KEY")
    public String key;

    @Transient
    private List<Log> errors;

    public Ticket() {
        this.createdAt = LocalDateTime.now();
    }

    @Data
    public static class IdClass implements Serializable {

        public String bilhete;
        public Date dataEmbarque;
    }

    @Override
    public String toString() {

        String iata = Optional.ofNullable(this.iataAgencia).isPresent() ? "" : "0";
        String line = Optional.ofNullable(this.lineFile).isPresent() ? "" : "0";

        String reg = MessageFormat.format("{0};{1};{2};{3};{4};{5};{6};{7};{8};{9};{10};{11};{12};{13};{14};{15};{16};{17};{18};{19};{20}",
                line.replaceAll(".", ""),
                Utils.formatDate(this.dataEmissao),
                Utils.formatDate(this.dataEmbarque),
                this.horaEmbarque,
                this.ciaBilhete,
                this.trecho,
                this.origem,
                this.destino,
                this.cupom,
                this.bilheteBehavior,
                this.tipo,
                this.cabine,
                this.ciaVoo,
                this.valorBrl,
                this.empresa,
                this.cnpj,
                iata.replaceAll(".", ""),
                this.baseVenda,
                this.qtdPax,
                this.numVoo,
                this.consolidada);

        if (this.layout.equals(TicketLayoutEnum.FULL)) {
            reg += MessageFormat.format(";{0};{1};{2};{3};{4};{5};{6};{7};{8};{9};{10};{11};{12};{13};{14};{15};{16};{17};{18};{19};{20};{21};{22};{23};{24};{25};{26};{27};{28}",
                    Utils.formatDate(this.dataExtracao),
                    this.horaEmissao,
                    Utils.formatDate(this.dataReserva),
                    this.horaReserva,
                    this.horaPouso,
                    this.baseTarifaria,
                    this.tktDesignator,
                    this.familiaTarifaria,
                    this.classeTarifa,
                    this.classeServico,
                    this.ondDirecional,
                    this.tourCode,
                    this.rtOw,
                    this.valorUs,
                    this.tarifaPublica,
                    this.tarifaPublicUs,
                    this.pnrAgencia,
                    this.pnrCiaArea,
                    this.selfBookingOffiline,
                    this.nomePax,
                    this.tipoPax,
                    this.cpfPax,
                    this.emailPax,
                    this.cellPax,
                    this.tierFidelidadePax,
                    this.tipoPagamento,
                    this.digitoVerificadorCC,
                    this.grupoEmpresa,
                    this.grupoConsolidada);
        }

        return reg;
    }

    public void setId(Long id) {

        this.id = id;
        
        try {
            String bilheteBehavior = "";
            SimpleDateFormat formmaterDate = new SimpleDateFormat("ddMMyyyy", new Locale("pt", "BR"));
            String dataEmissao = formmaterDate.format(this.getDataEmissao());
            String ano = dataEmissao.substring(dataEmissao.length() - 1);
            String mes = dataEmissao.substring(2, 4);

            String sequencial = "";
            String ids = String.valueOf(this.getId());

            if (ids.length() < SIZE_BILHETE_BEHAVIOR) {
                sequencial = StringUtils.leftPad(ids, SIZE_BILHETE_BEHAVIOR, "0");
            } else {
                sequencial = ids.substring(ids.length() - SIZE_BILHETE_BEHAVIOR);
            }

            bilheteBehavior = this.getLayout().equals(TicketLayoutEnum.FULL) ? MessageFormat.format("2{0}{1}{2}", ano, mes, sequencial) : MessageFormat.format("1{0}{1}{2}", ano, mes, sequencial);            
            
            
            this.setBilheteBehavior(bilheteBehavior);
        } catch (Exception e) {
            Logger.getLogger(Ticket.class.getName()).log(Level.SEVERE, "[ generateBilheteBehavior ]", e);
        }

    }

}
