package com.core.behavior.validator;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.Validator;
import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.TypeErrorEnum;
import com.core.behavior.util.Utils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class ValidatorShortLayout implements IValidatorShortLayout {

    @Autowired
    private LogService logService;
    
    @Autowired    
    private TicketService ticketService;

    public static final List<String> layoutMin = Arrays.asList("dataEmissao", "dataEmbarque", "horaEmbarque", "ciaBilhete", "trecho", "origem", "destino", "cupom", "bilhete", "tipo", "cabine", "ciaVoo", "valorBrl", "empresa", "cnpj", "iataAgencia", "baseVenda", "qtdPax", "numVoo", "consolidada");

    private Ticket ticket;
    private List<Log> errors = new ArrayList<>();

    public static long countErrors;
    
//    public ValidatorShortLayout(Ticket ticket, LogService logService, TicketService ticketService ) {
//        this.ticket = ticket;
//        this.ticketService = ticketService;
//        this.logService = logService;
//    }
    
    
    private static synchronized void countLog(){
        ++ValidatorShortLayout.countErrors;
    }
    

    private void generateLog(Ticket t, String message, String field) {
        Log log = new Log();
        log.setCreatedAt(LocalDateTime.now());
        log.setFileId(this.ticket.getFileId());
        log.setFieldName(field);
        log.setMessageError(message);
        log.setLineNumber(t.getLineFile());
        log.setRecordContent(ticket.toString());
        log.setType(TypeErrorEnum.RECORD);
        
        Optional<Log>op = logService.findByFileIdAndField(field, ticket.getFileId());
        
        if(op.isPresent()){
            Log l = op.get();
            l.setMessageError(l.getMessageError() + "\n" + message);
            logService.saveLog(l);
        }else{
            errors.add(log);
        }
        
        
        
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
        String trecho = ticket.getTrecho();
        
        
        String [] tr = trecho.split("/");
        
        if(tr.length <= 1){
            message.append("Incorreto a composição do trecho faltando '/'");
            this.generateLog(ticket, message.toString(), "trecho");
            return this;
        }
        
        
        String origem = tr[0];
        String destino = tr[tr.length - 1];
        

        if (trecho.length() < 7) {
            message.append("Incorreto o numero de carateres alfanumérico da compania aérea.\n");
        }

        if (!ticket.getDestino().equals(destino)) {
            message.append("Incorreto não contem o destino no final do trecho.\n");
        }

        if (!ticket.getOrigem().equals(origem)) {
            message.append("Incorreto não contem a origem no inicio do trecho.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "trecho");
        }

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
            message.append("Bilhete com o valor acima de 11  caracteres.\n");
        }

        String regex = "(0){" + bilhete.length() + "}";
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

        if (Optional.ofNullable(tipo).isPresent()) {
            if (tipo.length() > 1) {
                message.append("Maior que 1 caracter.\n");
            }

            if (!(tipo.equals("N") || tipo.equals("I"))) {
                message.append("Caracater encontrado não é N ou I.\n");
            }

            if (message.length() > 0) {
                this.generateLog(ticket, message.toString(), "tipo");
            }
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkClasseCabine() {

        StringBuilder message = new StringBuilder();
        String cabine = ticket.getCabine();

        if (Optional.ofNullable(cabine).isPresent()) {

            if (cabine.length() > 1) {
                message.append("Maior que 1 caracter.\n");
            }

            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(cabine);
            m.find();

            if (m.matches()) {
                message.append("Digito encontrado no campo.\n");
            }

            if (message.length() > 0) {
                this.generateLog(ticket, message.toString(), "cabine");
            }
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkCiaVoo() {

        StringBuilder message = new StringBuilder();
        String voo = ticket.getCiaVoo();

        if (Optional.ofNullable(voo).isPresent()) {
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

        Validator<String> validator = new CNPJValidator(true);
        StringBuilder message = new StringBuilder();

        if (Optional.ofNullable(cnpj).isPresent() && cnpj.length() > 0) {
            if (cnpj.length() < 18) {
                message.append("Cnpj inválido abaixo de 18 caracteres.\n");
            }

            if (cnpj.length() > 18) {
                message.append("Cnpj inválido acima de 18 caracteres.\n");
            }

            if (!validator.isEligible(cnpj)) {
                message.append("Cnpj ilegivel.\n");
            }

            if (validator.invalidMessagesFor(cnpj).size() > 0) {
                String messages = validator.invalidMessagesFor(cnpj).stream().map(v -> v.getMessage()).collect(Collectors.joining("\n"));
                message.append(messages);
            }

            if (message.length() > 0) {
                this.generateLog(ticket, message.toString(), "cnpj");
            }
        }
        return this;
    }

    @Override
    public IValidatorShortLayout checkIataAgenciaEmissora() {

        StringBuilder message = new StringBuilder();
        if (Optional.ofNullable(ticket.getIataAgencia()).isPresent()) {
            BigDecimal iata = BigDecimal.valueOf(ticket.getIataAgencia());
            if (iata.compareTo(BigDecimal.valueOf(1000000)) < 0) {
                message.append("Numero abaixo dos 7 digitos.\n");
            }

            if (iata.compareTo(BigDecimal.valueOf(99000000)) > 0) {
                message.append("Numero acima dos 8 digitos.\n");
            }

            if (message.length() > 0) {
                this.generateLog(ticket, message.toString(), "iataAgencia");
            }
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkBaseVenda() {

        StringBuilder message = new StringBuilder();
        String base = ticket.getBaseVenda();

        if (Optional.ofNullable(base).isPresent()) {

            if (base.length() > 3 || base.length() < 3) {
                message.append("Diferente de 3 caracteres.\n");
            }

            if (base.length() > 3 || base.length() < 3) {
                message.append("Diferente de 3 caracteres.\n");
            }

            Pattern p = Pattern.compile("\\d*");
            Matcher m = p.matcher(base);
            m.find();

            if (m.group().length() > 0) {
                message.append("Digito encontrado no campo.\n");
            }

            if (message.length() > 0) {
                this.generateLog(ticket, message.toString(), "iataAgencia");
            }
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkQtdPax() {

        StringBuilder message = new StringBuilder();

        ticket.setQtdPax(Optional.ofNullable(ticket.getQtdPax()).isPresent() ? ticket.getQtdPax() : 1L);

        if (ticket.getQtdPax() == 0l) {
            message.append("QtdPax iqual a zero.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "qtdPax");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkNumVoo() {

        StringBuilder message = new StringBuilder();

        if (ticket.getNumVoo() == 0l) {
            message.append("NumVoo iqual a zero.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "numVoo");
        }

        return this;
    }

    @Override
    public IValidatorShortLayout checkAgenciaConsolidada() {
        return this;
    }

    @Override
    public void validate(Ticket ticket) {
        this.ticket = ticket;
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
            
            TicketStatusEnum status = !errors.isEmpty() ? TicketStatusEnum.UNAPPROVED : TicketStatusEnum.APPROVED;
            this.ticket.setStatus(status);
            ticket = this.ticketService.save(ticket);
            final long idTicket = ticket.getId();
            
            if (!errors.isEmpty()) {                
                errors.parallelStream().forEach(e->{                
                    e.setTicketId(idTicket);
                });
                
                logService.saveBatch(errors);
                ValidatorShortLayout.countLog();
            }        
            
            
            

        } catch (Exception e) {
            Logger.getLogger(ValidatorShortLayout.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    
  
}

