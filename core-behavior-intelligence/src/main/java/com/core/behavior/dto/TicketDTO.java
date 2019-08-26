
package com.core.behavior.dto;

import com.core.behavior.util.TicketLayoutEnum;
import java.text.MessageFormat;
import lombok.Data;

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
    
    public String toString(){
        
        String reg = MessageFormat.format("{0}[col]{1}[col]{2}[col]{3}[col]{4}[col]{5}[col]{6}[col]{7}[col]{8}[col]{9}[col]{10}[col]{11}[col]{12}[col]{13}[col]{14}[col]{15}[col]{16}[col]{17}[col]{18}[col]{19}[col]{20}",
                this.lineFile,
                this.dataEmissao,
                this.dataEmbarque,
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

        if (this.layout.equals(TicketLayoutEnum.FULL.toString())) {
            reg += MessageFormat.format("[col]{0}[col]{1}[col]{2}[col]{3}[col]{4}[col]{5}[col]{6}[col]{7}[col]{8}[col]{9}[col]{10}[col]{11}[col]{12}[col]{13}[col]{14}[col]{15}[col]{16}[col]{17}[col]{18}[col]{19}[col]{20}[col]{21}[col]{22}[col]{23}[col]{24}[col]{25}[col]{26}[col]{27}[col]{28}", 
                    this.dataExtracao,
                    this.horaEmissao,
                    this.dataReserva,
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
}
