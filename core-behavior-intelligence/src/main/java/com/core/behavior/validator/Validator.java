package com.core.behavior.validator;

import br.com.caelum.stella.validation.CNPJValidator;

import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.LogService;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TypeErrorEnum;
import com.core.behavior.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.beanio.StreamFactory;

/**
 *
 * @author thiag
 */
public class Validator implements IValidator {

    private LogService logService;

    public static final List<String> layoutMin = Arrays.asList("dataEmissao", "dataEmbarque", "horaEmbarque", "ciaBilhete", "trecho", "origem", "destino", "cupom", "bilhete", "tipo", "cabine", "ciaVoo", "valorBrl", "empresa", "cnpj", "iataAgencia", "baseVenda", "qtdPax", "numVoo", "consolidada");

    private Ticket ticket;
    private List<Log> errors = new ArrayList<>();

    public static long countErrors;
    private static Properties props = new Properties();

    static {
        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream is = factory.getClass().getClassLoader().getResourceAsStream("beanio/layoutMinimoMessages.properties");
            props.load(is);
        } catch (IOException ex) {
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Validator() {

    }

    public Validator(LogService logService) {
        this.logService = logService;
    }

    private static synchronized void countLog() {
        ++com.core.behavior.validator.Validator.countErrors;
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
        errors.add(log);
    }

    @Override
    public IValidator checkDataEmissao() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime emissao = Utils.dateToLocalDateTime(ticket.getDataEmissao());
        LocalDateTime voo = Utils.dateToLocalDateTime(ticket.getDataEmbarque());
        int countError = 0;

        if (emissao.isAfter(now)) {
            countError++;
        }
        if (emissao.isAfter(voo)) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.dataEmissao.type"), "dataEmissao");
        }

        return this;
    }

    @Override
    public IValidator checkDataVoo() {

        LocalDateTime emissao = Utils.dateToLocalDateTime(ticket.getDataEmissao());
        LocalDateTime dataLimite = Utils.dateToLocalDateTime(ticket.getDataEmissao()).plusDays(360);
        LocalDateTime dataVoo = Utils.dateToLocalDateTime(ticket.getDataEmbarque());
        int countError = 0;

        if (dataVoo.isAfter(dataLimite)) {
            countError++;
        }

        if (dataVoo.isBefore(emissao)) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.dataEmbarque.type"), "dataEmbarque");
        }

        return this;
    }

    @Override
    public IValidator checkHoraVoo() {
        return this;
    }

    @Override
    public IValidator checkCiaBilhete() {

        int countError = 0;

        if (ticket.getCiaBilhete().length() > 2 || ticket.getCiaBilhete().length() < 1) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.ciaBilhete.type"), "ciaBilhete");
        }

        return this;
    }

    @Override
    public IValidator checkTrechoTkt() {

        int countError = 0;

        String trecho = ticket.getTrecho();

        String[] tr = trecho.split("/");

        if (tr.length <= 1) {
            countError++;
        }

        if (trecho.length() < 7) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.trecho.type"), "trecho");
        }

        return this;
    }

    @Override
    public IValidator checkAtoOrigem() {

        String destino = ticket.getDestino();
        String origem = ticket.getOrigem();

        int countError = 0;

        if (destino.equals(origem)) {
            countError++;
        }

        if (origem.length() < 3 || origem.length() > 3) {
            countError++;
        }

        Pattern p = Pattern.compile("\\d*");
        Matcher m = p.matcher(origem);
        m.find();

        if (m.group().length() > 0) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.origem.type"), "origem");
        }
        return this;
    }

    @Override
    public IValidator checkAtoDestino() {

        String destino = ticket.getDestino();
        String origem = ticket.getOrigem();
        int countError = 0;

        if (destino.equals(origem)) {
            countError++;
        }

        if (destino.length() < 3 || destino.length() > 3) {
            countError++;
        }

        Pattern p = Pattern.compile("\\d*");
        Matcher m = p.matcher(destino);
        m.find();

        if (m.group().length() > 0) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.destino.type"), "destino");
        }

        return this;
    }

    @Override
    public IValidator checkNumeroCupom() {

        int countError = 0;

        if (ticket.getCupom().equals(0l)) {
            countError++;
        }

        if (ticket.getCupom() >= 100) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.cupom.type"), "cupom");
        }
        return this;
    }

    @Override
    public IValidator checkBilhete() {

        int countError = 0;

        String bilhete = ticket.getBilhete();

        if (bilhete.length() < 3) {
            countError++;
        }

        if (bilhete.length() > 11) {
            countError++;
        }

        String regex = "(0){" + bilhete.length() + "}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(bilhete);
        m.find();

        if (m.matches()) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.bilhete.type"), "bilhete");
        }

        return this;
    }

    @Override
    public IValidator checkTipoVenda() {

        String tipo = ticket.getTipo();
        int countError = 0;

        if (Optional.ofNullable(tipo).isPresent()) {
            if (tipo.length() > 1) {
                countError++;
            }

            if (!(tipo.equals("N") || tipo.equals("I"))) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticket, props.getProperty("fielderror.ticket.tipo.type"), "tipo");
            }
        }

        return this;
    }

    @Override
    public IValidator checkClasseCabine() {

        int countError = 0;
        String cabine = ticket.getCabine();

        if (Optional.ofNullable(cabine).isPresent()) {

            if (cabine.length() > 1) {
                countError++;
            }

            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(cabine);
            m.find();

            if (m.matches()) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticket, props.getProperty("fielderror.ticket.cabine.type"), "cabine");
            }
        }

        return this;
    }

    @Override
    public IValidator checkCiaVoo() {

        int countError = 0;
        String voo = ticket.getCiaVoo();

        if (Optional.ofNullable(voo).isPresent()) {
            if (voo.length() > 2) {
                countError++;
            }

            if (voo.length() < 2) {
                countError++;
            }

            Pattern p = Pattern.compile("\\d*");
            Matcher m = p.matcher(voo);
            m.find();

            if (m.group().length() > 0) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticket, props.getProperty("fielderror.ticket.ciaVoo.type"), "ciaVoo");
            }
        }

        return this;
    }

    @Override
    public IValidator checkValorBrl() {

        int countError = 0;
        if (ticket.getValorBrl() == 0) {
            countError++;
        }

        if (ticket.getValorBrl() < 0) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.valorBrl.type"), "valorBrl");
        }

        return this;
    }

    @Override
    public IValidator checkClienteEmpresa() {
        return this;
    }

    @Override
    public IValidator checkCnpjClienteEmpresa() {

        String cnpj = ticket.getCnpj();

        br.com.caelum.stella.validation.Validator<String> validator = new CNPJValidator(true);
        int countError = 0;

        if (Optional.ofNullable(cnpj).isPresent() && cnpj.length() > 0) {
            if (cnpj.length() < 18) {
                countError++;
            }

            if (cnpj.length() > 18) {
                countError++;
            }

            if (!validator.isEligible(cnpj)) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticket, props.getProperty("fielderror.ticket.cnpj.type"), "cnpj");
            }
        }
        return this;
    }

    @Override
    public IValidator checkIataAgenciaEmissora() {

        int countError = 0;
        if (Optional.ofNullable(ticket.getIataAgencia()).isPresent()) {
            BigDecimal iata = BigDecimal.valueOf(ticket.getIataAgencia());
            if (iata.compareTo(BigDecimal.valueOf(1000000)) < 0) {
                countError++;
            }

            if (iata.compareTo(BigDecimal.valueOf(99000000)) > 0) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticket, props.getProperty("fielderror.ticket.iataAgencia.type"), "iataAgencia");
            }
        }

        return this;
    }

    @Override
    public IValidator checkBaseVenda() {

        int countError = 0;
        String base = ticket.getBaseVenda();

        if (Optional.ofNullable(base).isPresent()) {

            if (base.length() > 3 || base.length() < 3) {
                countError++;
            }

            if (base.length() > 3 || base.length() < 3) {
                countError++;
            }

            Pattern p = Pattern.compile("\\d*");
            Matcher m = p.matcher(base);
            m.find();

            if (m.group().length() > 0) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticket,  props.getProperty("fielderror.ticket.baseVenda.type"), "baseVenda");
            }
        }

        return this;
    }

    @Override
    public IValidator checkQtdPax() {

        int countError = 0;

        ticket.setQtdPax(Optional.ofNullable(ticket.getQtdPax()).isPresent() ? ticket.getQtdPax() : 1L);

        if (ticket.getQtdPax() == 0l) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticket, props.getProperty("fielderror.ticket.qtdPax.type"), "qtdPax");
        }

        return this;
    }

    @Override
    public IValidator checkNumVoo() {

//        int countError = 0;
//
//        if (ticket.getNumVoo() == 0l) {
//            countError++;
//        }
//
//        if (countError > 0) {
//            this.generateLog(ticket, props.getProperty("fielderror.ticket.numVoo.type"), "numVoo");
//        }

        return this;
    }

    @Override
    public IValidator checkAgenciaConsolidada() {
        return this;
    }

    @Override
    public IValidator checkDataExtracao() {
        return this;
    }

    @Override
    public IValidator checkHoraEmissao() {
        return this;
    }

    @Override
    public IValidator checkDataReserva() {
        return this;
    }

    @Override
    public IValidator checkHoraReserva() {
        return this;
    }

    @Override
    public IValidator checkHoraPouso() {
        return this;
    }

    @Override
    public IValidator checkBaseTarifaria() {
        return this;
    }

    @Override
    public IValidator checkTktDesignator() {
        return this;
    }

    @Override
    public IValidator checkFamiliaTarifaria() {
        return this;
    }

    @Override
    public IValidator checkClasseTarifa() {
        return this;
    }

    @Override
    public IValidator checkClasseServico() {
        return this;
    }

    @Override
    public IValidator checkOndDirecional() {
        return this;
    }

    @Override
    public IValidator checkTourCode() {
        return this;
    }

    @Override
    public IValidator checkRtOw() {

        StringBuilder message = new StringBuilder();

        if (ticket.getRtOw() != null && (ticket.getRtOw().equals("RT") || ticket.getRtOw().equals("OW"))) {
            message.append("Rt_Ow inválido.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticket, message.toString(), "rtOw");
        }

        return this;
    }

    @Override
    public IValidator checkValorUs() {
        return this;
    }

    @Override
    public IValidator checkTarifaPublica() {
        return this;
    }

    @Override
    public IValidator checkTarifaPublicaUs() {
        return this;
    }

    @Override
    public IValidator checkPnrAgencia() {
        return this;
    }

    @Override
    public IValidator checkPnrCiaArea() {
        return this;
    }

    @Override
    public IValidator checkSelfBookingOffiline() {
        return this;
    }

    @Override
    public IValidator checkNomePax() {
        return this;
    }

    @Override
    public IValidator checkTipoPax() {
        return this;
    }

    @Override
    public IValidator checkCpfPax() {
        return this;
    }

    @Override
    public IValidator checkEmailPax() {
        return this;
    }

    @Override
    public IValidator checkCellPax() {
        return this;
    }

    @Override
    public IValidator checkTierFidelidadePax() {
        return this;
    }

    @Override
    public IValidator checkTipoPagamento() {
        return this;
    }

    @Override
    public IValidator checkDigitoVerificador() {
        return this;
    }

    @Override
    public IValidator checkGrupoEmpresa() {
        return this;
    }

    @Override
    public IValidator checkGrupoConsolida() {
        return this;
    }

    @Override
    public void validate(Ticket ticket, List<Ticket> out) {
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

            if (ticket.getLayout().equals(TicketLayoutEnum.FULL)) {
                this.checkDataExtracao()
                        .checkHoraEmissao()
                        .checkDataReserva()
                        .checkHoraReserva()
                        .checkHoraPouso()
                        .checkBaseTarifaria()
                        .checkTktDesignator()
                        .checkFamiliaTarifaria()
                        .checkClasseTarifa()
                        .checkClasseServico()
                        .checkOndDirecional()
                        .checkTourCode()
                        .checkRtOw()
                        .checkValorUs()
                        .checkTarifaPublica()
                        .checkTarifaPublicaUs()
                        .checkPnrAgencia()
                        .checkPnrCiaArea()
                        .checkSelfBookingOffiline()
                        .checkNomePax()
                        .checkTipoPax()
                        .checkCpfPax()
                        .checkEmailPax()
                        .checkCellPax()
                        .checkTierFidelidadePax()
                        .checkTipoPagamento()
                        .checkDigitoVerificador()
                        .checkGrupoEmpresa()
                        .checkGrupoConsolida();
            }

            if (!errors.isEmpty()) {
                logService.saveAll(errors);
                com.core.behavior.validator.Validator.countLog();
            } else {
                out.add(ticket);
            }

        } catch (Exception e) {
            Logger.getLogger(com.core.behavior.validator.Validator.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
