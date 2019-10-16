package com.core.behavior.dto;

import com.core.behavior.util.TicketLayoutEnum;
import java.text.MessageFormat;
import lombok.Data;
import org.apache.spark.api.java.Optional;

/**
 *
 * @author thiag
 */
@Data
public class TicketDTO {

    public String ondDirecional;
    public String consolidada;
    public String destino;
    public String origem;
    public String baseTarifaria;
    public String baseVenda;
    public String bilhete;
    public String ciaBilhete;
    public String ciaVoo;
    public String cabine;
    public String classeServico;
    public String classeTarifa;
    public String empresa;
    public String cnpj;
    public String cpfPax;
    public String dataEmissao;
    public String dataExtracao;
    public String dataReserva;
    public String dataEmbarque;
    public String digitoVerificadorCC;
    public String familiaTarifaria;
    public String horaEmissao;
    public String horaReserva;
    public String horaEmbarque;
    public String iataAgencia;
    public String nomeCliente;
    public String nomePax;
    public String cupom;
    public String numVoo;
    public String pnrAgencia;
    public String pnrCiaArea;
    public String qtdPax;
    public String rtOw;
    public String selfBookingOffiline;
    public String tarifaPublicUs;
    public String tarifaPublica;
    public String tierFidelidadePax;
    public String tipoPagamento;
    public String tipoPax;
    public String tipo;
    public String tourCode;
    public String trecho;
    public String valorBrl;
    public String valorUs;
    public String createdAt;
    public String fileId;
    public String id;
    public String status;
    public String lineFile;
    public String layout;
    public String horaPouso;
    public String tktDesignator;
    public String emailPax;
    public String cellPax;
    public String grupoEmpresa;
    public String grupoConsolidada;
    public String codigoAgencia;

    public String toString() {

        String reg = MessageFormat.format("{0}[col]{1}[col]{2}[col]{3}[col]{4}[col]{5}[col]{6}[col]{7}[col]{8}[col]{9}[col]{10}[col]{11}[col]{12}[col]{13}[col]{14}[col]{15}[col]{16}[col]{17}[col]{18}[col]{19}[col]{20}",
                this.formatStringEmpty(this.lineFile),
                this.formatStringEmpty(this.dataEmissao),
                this.formatStringEmpty(this.dataEmbarque),
                this.formatStringEmpty(this.horaEmbarque),
                this.formatStringEmpty(this.ciaBilhete),
                this.formatStringEmpty(this.trecho),
                this.formatStringEmpty(this.origem),
                this.formatStringEmpty(this.destino),
                this.formatStringEmpty(this.cupom),
                this.formatStringEmpty(this.bilhete),
                this.formatStringEmpty(this.tipo),
                this.formatStringEmpty(this.cabine),
                this.formatStringEmpty(this.ciaVoo),
                this.formatStringEmpty(this.valorBrl),
                this.formatStringEmpty(this.empresa),
                this.formatStringEmpty(this.cnpj),
                this.formatStringEmpty(this.iataAgencia),
                this.formatStringEmpty(this.baseVenda),
                this.formatStringEmpty(this.qtdPax),
                this.formatStringEmpty(this.numVoo),
                this.formatStringEmpty(this.consolidada));

        if (this.layout.equals(TicketLayoutEnum.FULL.toString())) {
            reg += MessageFormat.format("[col]{0}[col]{1}[col]{2}[col]{3}[col]{4}[col]{5}[col]{6}[col]{7}[col]{8}[col]{9}[col]{10}[col]{11}[col]{12}[col]{13}[col]{14}[col]{15}[col]{16}[col]{17}[col]{18}[col]{19}[col]{20}[col]{21}[col]{22}[col]{23}[col]{24}[col]{25}[col]{26}[col]{27}[col]{28}",
                    this.formatStringEmpty(this.dataExtracao),
                    this.formatStringEmpty(this.horaEmissao),
                    this.formatStringEmpty(this.dataReserva),
                    this.formatStringEmpty(this.horaReserva),
                    this.formatStringEmpty(this.horaPouso),
                    this.formatStringEmpty(this.baseTarifaria),
                    this.formatStringEmpty(this.tktDesignator),
                    this.formatStringEmpty(this.familiaTarifaria),
                    this.formatStringEmpty(this.classeTarifa),
                    this.formatStringEmpty(this.classeServico),
                    this.formatStringEmpty(this.ondDirecional),
                    this.formatStringEmpty(this.tourCode),
                    this.formatStringEmpty(this.rtOw),
                    this.formatStringEmpty(this.valorUs),
                    this.formatStringEmpty(this.tarifaPublica),
                    this.formatStringEmpty(this.tarifaPublicUs),
                    this.formatStringEmpty(this.pnrAgencia),
                    this.formatStringEmpty(this.pnrCiaArea),
                    this.formatStringEmpty(this.selfBookingOffiline),
                    this.formatStringEmpty(this.nomePax),
                    this.formatStringEmpty(this.tipoPax),
                    this.formatStringEmpty(this.cpfPax),
                    this.formatStringEmpty(this.emailPax),
                    this.formatStringEmpty(this.cellPax),
                    this.formatStringEmpty(this.tierFidelidadePax),
                    this.formatStringEmpty(this.tipoPagamento),
                    this.formatStringEmpty(this.digitoVerificadorCC),
                    this.formatStringEmpty(this.grupoEmpresa),
                    this.formatStringEmpty(this.grupoConsolidada));
        }

        return reg;
    }

    public String formatStringEmpty(String value) {
        return (Optional.ofNullable(value).isPresent() && value.length() > 0) ? value : "[empty]";
    }
}
