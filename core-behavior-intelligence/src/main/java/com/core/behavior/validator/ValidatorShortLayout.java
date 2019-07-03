package com.core.behavior.validator;

import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.RecordErrorEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.TypeErrorEnum;
import com.core.behavior.util.Utils;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
public class ValidatorShortLayout implements IValidatorShortLayout {

    @Autowired
    private LogService logService;

    @Autowired
    private TicketService ticketService;

    public static final List<String> layoutMin = Arrays.asList("dataEmissao", "dataEmbarque", "horaEmbarque", "ciaBilhete", "trecho", "origem", "destino", "cupom", "bilhete", "tipo", "cabine", "ciaVoo", "valorBrl", "empresa", "cnpj", "iataAgencia", "baseVenda", "qtdPax", "numVoo", "consolidada");

    private Ticket ticket;
    private List<RecordErrorEnum> errors = new ArrayList<>();

    public ValidatorShortLayout(Ticket ticket) {
        this.ticket = ticket;
    }

    private void generateLog(Ticket t, String message, String field) {
        Log log = new Log();
        log.setCreatedAt(LocalDateTime.now());
        log.setFileId(this.ticket.getFileId());
        log.setFieldName(field);
        log.setMessageError(message);
        log.setType(TypeErrorEnum.RECORD);
        logService.saveLog(log);
    }

    @Override
    public IValidatorShortLayout checkDataEmissao() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime emissao = Utils.dateToLocalDateTime(ticket.getDataEmissao());
        LocalDateTime voo = Utils.dateToLocalDateTime(ticket.getDataEmbarque());

        //:TODO DATA RESERVA NÂO TEM NO LAYOUT PEQUENO
        if (emissao.isAfter(now) || emissao.isAfter(voo)) {
            this.generateLog(ticket, "Data da emissão é maior que a data da extratção.\nOu Data da emissão é maior que a data do embarque", "dataEmissao");
        }
        return this;
    }

    @Override
    public IValidatorShortLayout checkDataVoo() {

        LocalDateTime emissao = Utils.dateToLocalDateTime(ticket.getDataEmissao());
        LocalDateTime dataLimite = Utils.dateToLocalDateTime(ticket.getDataEmissao()).plusDays(360);
        LocalDateTime dataVoo = Utils.dateToLocalDateTime(ticket.getDataEmbarque());
        StringBuilder message = new StringBuilder();

        if (dataVoo.isAfter(dataLimite)) {
            message.append("Data embarque ultrapassa os 365 dias da data de emissão do ticket,\n");
        }

        if (dataVoo.isBefore(emissao)) {
            message.append("Data embarque menor que a data de emissão do ticket\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "dataEmbarque");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkHoraVoo() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkCiaBilhete() {

        StringBuilder message = new StringBuilder();

        //:TODO FALTA FAZER UM CHECK NA BASE
        if (ticket.getCiaBilhete().length() > 2 || ticket.getCiaBilhete().length() < 1) {
            message.append("Incorreto o numero de carateres alfanumérico da compania aérea");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "ciaBilhete");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkTrechoTkt() {

        StringBuilder message = new StringBuilder();

        if (ticket.getTrecho().length() < 7) {
            message.append("Incorreto o numero de carateres alfanumérico da compania aérea");
        }

//        
//        Inserir os códigos IATA dos aeroportos de origem (ATO_ORIGEM) e destino (ATO_DESTINO);
//  Correto os códigos de aeroportos separados pelo caractere "/" barra;
//  Correto campo estar preenchido;
//  Correto mínimo de 07 caracteres, separados a cada três caracteres com apenas uma barra;
//  Correto o código IATA quando na sequencia ser diferentes;
//  Incorreto o código IATA quando na sequencia ser repetido;
//  Incorreto o campo estar em branco;
//  Incorreto o campo ter espaço, número ou caracter diferente de "/" barra;
//  Incorreto o campo iniciar com número ou barra;
        return this;
    }

    @Override
    public IValidatorShortLayout checkAtoOrigem() {

        String destino = ticket.getDestino();
        String origem = ticket.getOrigem();
        StringBuilder message = new StringBuilder();

        if (destino.equals(origem)) {
            message.append("Origem é iqual ao destino.\n");
        }

        if (origem.length() < 3 || origem.length() > 3) {
            message.append("Quantidade de caractere invalido.\n");
        }

        Pattern p = Pattern.compile("\\d*");
        Matcher m = p.matcher(origem);
        m.find();

        if (m.group().length() > 0) {
            message.append("Digito encontrado no campo.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "origem");
        }
        return this;
    }

    @Override
    public IValidatorShortLayout checkAtoDestino() {

        String destino = ticket.getDestino();
        String origem = ticket.getOrigem();
        StringBuilder message = new StringBuilder();

        if (destino.equals(origem)) {
            message.append("Destino iqual a origem.\n");
        }

        if (destino.length() < 3 || destino.length() > 3) {
            message.append("Quantidade de caractere invalido.\n");
        }

        Pattern p = Pattern.compile("\\d*");
        Matcher m = p.matcher(destino);
        m.find();

        if (m.group().length() > 0) {
            message.append("Digito encontrado no campo.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "destino");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkNumeroCupom() {

        StringBuilder message = new StringBuilder();

        if (ticket.getCupom().equals(0l)) {
            message.append("Digito encontrado iqual a 0.\n");
        }

        if (ticket.getCupom() >= 100) {
            message.append("Maior que dois digitos.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "cupom");
        }
        return this;
    }

    @Override
    public IValidatorShortLayout checkBilhete() {

        StringBuilder message = new StringBuilder();

        String bilhete = ticket.getBilhete();

        if (bilhete.length() < 3) {
            message.append("Bilhete com o valor abaixo de 3  caracteres.\n");
        }

        if (bilhete.length() > 11) {
            message.append("Bilhete com o valor acima de1  caracteres.\n");
        }

        String regex = MessageFormat.format("(0){0}", bilhete.length());
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(bilhete);
        m.find();

        if (m.matches()) {
            message.append("Bilhete com apenas digitos 0.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "bilhete");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkTipoVenda() {

        String tipo = ticket.getTipo();
        StringBuilder message = new StringBuilder();

        if (tipo.length() > 1) {
            message.append("Maior que 1 caracter.\n");
        }

        if (!tipo.equals("N") || !tipo.equals("I")) {
            message.append("Caracater encontrado não é N ou I.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "tipo");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkClasseCabine() {

        StringBuilder message = new StringBuilder();
        String cabine = ticket.getCabine();

        if (cabine.length() > 1) {
            message.append("Maior que 1 caracter.\n");
        }

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(cabine);
        m.find();

        if (m.group().length() > 0) {
            message.append("Digito encontrado no campo.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "cabine");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkCiaVoo() {

        StringBuilder message = new StringBuilder();
        String voo = ticket.getCiaVoo();

        if (voo.length() > 2) {
            message.append("Maior que 2 caracteres.\n");
        }

        if (voo.length() < 2) {
            message.append("Menor que 2 caracteres.\n");
        }

        Pattern p = Pattern.compile("\\d*");
        Matcher m = p.matcher(voo);
        m.find();

        if (m.group().length() > 0) {
            message.append("Digito encontrado no campo.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "ciaVoo");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkValorBrl() {

        StringBuilder message = new StringBuilder();

        if (ticket.getValorBrl() == 0) {
            message.append("Valor zerado.\n");
        }

        if (ticket.getValorBrl() < 0) {
            message.append("Valor negativo.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "valorBrl");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkClienteEmpresa() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkCnpjClienteEmpresa() {
        
        String cnpj = ticket.getCnpj();
     
        
        
        return this;
    }

    @Override
    public IValidatorShortLayout checkIataAgenciaEmissora() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkBaseVenda() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkQtdPax() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkNumVoo() {
        return this;
    }

    @Override
    public IValidatorShortLayout checkAgenciaConsolidada() {
        return this;
    }

    @Override
    public void validate() {
        try {

            this.checkDataEmissao().
                    checkDataVoo().
                    checkHoraVoo().
                    checkCiaBilhete().
                    checkTrechoTkt().
                    checkAtoOrigem().
                    checkAtoDestino().
                    checkNumeroCupom().
                    checkBilhete().
                    checkTipoVenda().
                    checkClasseCabine().
                    checkCiaVoo().
                    checkValorBrl().
                    checkClienteEmpresa().
                    checkCnpjClienteEmpresa().
                    checkIataAgenciaEmissora().
                    checkBaseVenda().
                    checkQtdPax().
                    checkNumVoo().
                    checkAgenciaConsolidada();

            this.errors.forEach(e -> {
                Log log = new Log();
                log.setCreatedAt(LocalDateTime.now());
                log.setFileId(this.ticket.getFileId());
                log.setMessageError(e.message);
                log.setType(TypeErrorEnum.RECORD);
                logService.saveLog(log);
            });

            TicketStatusEnum status = !errors.isEmpty() ? TicketStatusEnum.UNAPPROVED : TicketStatusEnum.APPROVED;
            this.ticket.setStatus(status);
            this.ticketService.save(ticket);

        } catch (Exception e) {
            Log log = new Log();
            log.setCreatedAt(LocalDateTime.now());
            log.setFileId(this.ticket.getFileId());
            log.setMessageError(e.getMessage());
            log.setType(TypeErrorEnum.RECORD);
            logService.saveLog(log);
        }
    }

}
