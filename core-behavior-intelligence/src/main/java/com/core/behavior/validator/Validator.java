package com.core.behavior.validator;

import com.core.behavior.dto.TicketDTO;

import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TypeErrorEnum;
import com.core.behavior.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

   

    public static final List<String> layoutMin = Arrays.asList("dataEmissao", "dataEmbarque", "horaEmbarque", "ciaBilhete", "trecho", "origem", "destino", "cupom", "bilhete", "tipo", "cabine", "ciaVoo", "valorBrl", "empresa", "cnpj", "iataAgencia", "baseVenda", "qtdPax", "numVoo", "consolidada");

    private TicketDTO ticketDTO;
    private Ticket ticket = new Ticket();
    
    private SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));

    private final static int DATA_VOO_LIMITE_DIAS = 365;
    private final static String REGEX_HORA_VOO = "([01]?[0-9]|2[0-3]):([0-5][0-9]):?([0-5][0-9])?";
    private final static String REGEX_TRECHO = "([A-Z]{3}[/]*)+";
    private final static String REGEX_ORIGEM_DESTINO = "([A-Z]){3}";
    private final static String REGEX_CUPOM = "((^0*[1-9]$)|(^[1-9][0-9]$))";
    private final static String REGEX_BILHETE = "[A-Z0-9]{3,11}";
    private final static String REGEX_TIPO = "(I|N){1}";
    private final static String REGEX_CABINE = "[A-Z]{1}";
    private final static String REGEX_CIA_VOO = "([A-Z]{1}[0-9]{1})|([A-Z]{1}[A-Z]{1})|([0-9]{1}[A-Z]{1})";
    private final static String REGEX_VALOR_BRL = "^([0-9]{1,3}\\.?)+(,[0-9]{1,2})?$";
    private final static String REGEX_CNPJ = "(^\\d{2}.\\d{3}.\\d{3}/\\d{4}-\\d{2}$)";
    private final static String REGEX_IATA_AGENCIA = "[0-9]{7,8}";
    private final static String REGEX_BASE_VENDA = "[A-Z]{3}";
    private final static String REGEX_QTD_PAX = "((^0*[1-9]$)|(^[1-9][0-9]$))";
    private final static String REGEX_NUM_VOO = "([0-9]{2,4})|([^A-Z]{2,4})|(^\\d[A-Z]{2,3})|([A-Z]{1,3}\\d$)|(^\\D[0-9]{2,3})|([0-9]{1,3}\\D$)|(\\d\\D){2,4}|(\\D\\d){2,4}";

    
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
        this.formatter4.setLenient(false);
    }   

    private  void generateLog(TicketDTO t, String message, String field) {
        Log log = new Log();
        log.setCreatedAt(LocalDateTime.now());
        log.setFileId(Long.parseLong(this.ticketDTO.getFileId()));
        log.setFieldName(field);
        log.setMessageError(message);
        log.setLineNumber(Long.parseLong(t.getLineFile()));
        log.setRecordContent(ticketDTO.toString());
        log.setType(TypeErrorEnum.RECORD);
        ticket.getErrors().add(log);
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    public IValidator checkDataEmissao() {

        int countError = 0;

        Date dataEmbarque = null;
        Date dataEmissao = null;

        if (!Optional.ofNullable(ticketDTO.getDataEmissao()).isPresent()) {
            countError++;
        }

        if (countError == 0 && ticketDTO.getDataEmissao().length() != 10) {
            countError++;
        }

        try {
            dataEmbarque = formatter4.parse(ticketDTO.getDataEmbarque());
            dataEmissao = formatter4.parse(ticketDTO.getDataEmissao());
        } catch (ParseException e3) {
            countError++;
        }

        if (dataEmbarque != null && dataEmissao != null && countError == 0) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime emissao = this.dateToLocalDateTime(dataEmissao);
            LocalDateTime voo = this.dateToLocalDateTime(dataEmbarque);

            if (emissao.isAfter(now)) {
                countError++;
            }
            if (emissao.isAfter(voo)) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.dataEmissao.type"), "dataEmissao");
        } else {
            ticket.setDataEmissao(dataEmissao);
        }

        return this;
    }

    @Override
    public IValidator checkDataVoo() {

        int countError = 0;
        Date dataEmbarque = null;
        Date dataEmissao = null;

        if (!Optional.ofNullable(ticketDTO.getDataEmbarque()).isPresent()) {
            countError++;
        }

        try {
            dataEmbarque = formatter4.parse(ticketDTO.getDataEmbarque());
            dataEmissao = formatter4.parse(ticketDTO.getDataEmissao());
        } catch (ParseException e3) {
            countError++;
        }
        
        if(countError == 0 && dataEmbarque.getTime() < dataEmissao.getTime()){
            countError++;
        }
        
        if (countError == 0 && dataEmbarque != null && dataEmissao != null) {

            LocalDateTime emissao = Utils.dateToLocalDateTime(dataEmissao);
            LocalDateTime dataLimite = Utils.dateToLocalDateTime(dataEmissao).plusDays(DATA_VOO_LIMITE_DIAS);
            LocalDateTime dataVoo = Utils.dateToLocalDateTime(dataEmbarque);

            if (dataVoo.isAfter(dataLimite)) {
                countError++;
            }

            if (dataVoo.isBefore(emissao)) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.dataEmbarque.type"), "dataEmbarque");
        } else {
            ticket.setDataEmbarque(dataEmbarque);
        }

        return this;
    }

    @Override
    public IValidator checkHoraVoo() {

        Pattern p = Pattern.compile(REGEX_HORA_VOO);
        int countError = 0;

        if (!Optional.ofNullable(ticketDTO.getHoraEmbarque()).isPresent()) {
            countError++;
        }

        try {
            Matcher m = p.matcher(ticketDTO.getHoraEmbarque());
            if (!m.matches()) {
                countError++;
            }
        } catch (Exception e) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.horaEmbarque.type"), "horaEmbarque");
        } else {
            ticket.setHoraEmbarque(ticketDTO.getHoraEmbarque());
        }

        return this;
    }

    @Override
    public IValidator checkCiaBilhete() {

        int countError = 0;

        if (!Optional.ofNullable(ticketDTO.getCiaBilhete()).isPresent()) {
            countError++;
        }

        if (countError == 0 && ticketDTO.getCiaBilhete().length() > 2 || ticketDTO.getCiaBilhete().length() < 2) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.ciaBilhete.type"), "ciaBilhete");
        } else {
            ticket.setCiaBilhete(ticketDTO.getCiaBilhete());
        }

        return this;
    }

    @Override
    public IValidator checkTrechoTkt() {

        int countError = 0;
        Pattern p = Pattern.compile(REGEX_TRECHO);

        if (!Optional.ofNullable(ticketDTO.getTrecho()).isPresent()) {
            countError++;
        }

        String trecho = ticketDTO.getTrecho();

        Matcher matcher = p.matcher(trecho);

        if (countError == 0 && !matcher.matches()) {
            countError++;
        }

        if (trecho != null && trecho.length() < 7) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.trecho.type"), "trecho");
        } else {
            ticket.setTrecho(trecho);
        }

        return this;
    }

    @Override
    public IValidator checkAtoOrigem() {

        String destino = ticketDTO.getDestino();
        String origem = ticketDTO.getOrigem();

        int countError = 0;

        Pattern pattern = Pattern.compile(REGEX_ORIGEM_DESTINO);
        Matcher m = pattern.matcher(origem);

        if (!m.matches()) {
            countError++;
        }

        if (destino.equals(origem)) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.origem.type"), "origem");
        } else {
            ticket.setOrigem(origem);
        }

        return this;
    }

    @Override
    public IValidator checkAtoDestino() {

        String destino = ticketDTO.getDestino();
        String origem = ticketDTO.getOrigem();
        int countError = 0;

        Pattern pattern = Pattern.compile(REGEX_ORIGEM_DESTINO);
        Matcher m = pattern.matcher(destino);

        if (!m.matches()) {
            countError++;
        }

        if (destino.equals(origem)) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.destino.type"), "destino");
        } else {
            ticket.setDestino(destino);
        }

        return this;
    }

    @Override
    public IValidator checkNumeroCupom() {

        int countError = 0;

        Pattern pattern = Pattern.compile(REGEX_CUPOM);
        Matcher m = pattern.matcher(ticketDTO.getCupom());

        if (!m.matches()) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.cupom.type"), "cupom");
        } else {
            ticket.setCupom(Long.parseLong(ticketDTO.getCupom()));
        }

        return this;
    }

    @Override
    public IValidator checkBilhete() {

        int countError = 0;

        if (!Optional.ofNullable(ticketDTO.getBilhete()).isPresent()) {
            countError++;
        }

        if (countError == 0) {
            Pattern p = Pattern.compile(REGEX_BILHETE);
            Matcher m = p.matcher(ticketDTO.getBilhete());

            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.bilhete.type"), "bilhete");
        } else {
            ticket.setBilhete(ticketDTO.getBilhete());
        }

        return this;
    }

    @Override
    public IValidator checkTipoVenda() {

        String tipo = ticketDTO.getTipo();
        int countError = 0;

        if (!Optional.ofNullable(tipo).isPresent()) {
            countError++;
        }

        if (countError == 0) {
            Pattern p = Pattern.compile(REGEX_TIPO);
            Matcher m = p.matcher(tipo);

            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.tipo.type"), "tipo");
        } else {
            this.ticket.setTipo(tipo);
        }

        return this;
    }

    @Override
    public IValidator checkClasseCabine() {

        int countError = 0;
        String cabine = ticketDTO.getCabine();

        if (!Optional.ofNullable(cabine).isPresent()) {
            countError++;
        }

        if (countError == 0) {
            Pattern p = Pattern.compile(REGEX_CABINE);
            Matcher m = p.matcher(cabine);

            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.cabine.type"), "cabine");
        } else {
            this.ticket.setCabine(cabine);
        }

        return this;
    }

    @Override
    public IValidator checkCiaVoo() {

        int countError = 0;
        String voo = ticketDTO.getCiaVoo();

        if (!Optional.ofNullable(voo).isPresent()) {

            countError++;
        }

        if (countError == 0) {
            Pattern p = Pattern.compile(REGEX_CIA_VOO);
            Matcher m = p.matcher(voo);

            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.ciaVoo.type"), "ciaVoo");
        } else {
            ticket.setCiaVoo(voo);
        }

        return this;
    }

    @Override
    public IValidator checkValorBrl() {

        int countError = 0;
        Double value = null;

        String valor = ticketDTO.getValorBrl();
        if (!Optional.ofNullable(valor).isPresent()) {
            countError++;
        }

        if (countError == 0) {

            Pattern p = Pattern.compile(REGEX_VALOR_BRL);
            Matcher m = p.matcher(valor);

            if (!m.matches()) {
                countError++;
            }

            if (countError == 0) {
                try {
                    value = Double.valueOf(valor.replace(".", "").replace(",", "."));
                    if (value.equals(0.0d)) {
                        countError++;
                    }
                } catch (Exception e) {
                    countError++;
                }

            }

        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.valorBrl.type"), "valorBrl");
        } else {
            ticket.setValorBrl(value);
        }

        return this;
    }

    @Override
    public IValidator checkClienteEmpresa() {

        if (Optional.ofNullable(ticketDTO.getEmpresa()).isPresent()) {
            this.ticket.setEmpresa(ticketDTO.getEmpresa());
        } else {
            this.ticket.setEmpresa("");
        }

        return this;
    }

    @Override
    public IValidator checkCnpjClienteEmpresa() {

        String cnpj = ticketDTO.getCnpj();

        int countError = 0;

        if (cnpj != null && cnpj.length() > 0) {

            Pattern p = Pattern.compile(REGEX_CNPJ);
            Matcher m = p.matcher(cnpj);

            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.cnpj.type"), "cnpj");
        } else {
            ticket.setCnpj(cnpj);
        }

        return this;
    }

    @Override
    public IValidator checkIataAgenciaEmissora() {

        int countError = 0;
        Long value = null;
        String iataAgencia = ticketDTO.getIataAgencia();
        if (!Optional.ofNullable(iataAgencia).isPresent()) {
            countError++;
        }

        if (countError == 0) {
            Pattern p = Pattern.compile(REGEX_IATA_AGENCIA);
            Matcher m = p.matcher(iataAgencia);

            if (!m.matches()) {
                countError++;
            } else {
                value = Long.valueOf(iataAgencia);
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.iataAgencia.type"), "iataAgencia");
        } else {
            ticket.setIataAgencia(value);
        }

        return this;
    }

    @Override
    public IValidator checkBaseVenda() {

        int countError = 0;
        String base = ticketDTO.getBaseVenda();

        if (!Optional.ofNullable(base).isPresent()) {

            countError++;
        }

        if (countError == 0) {

            Pattern p = Pattern.compile(REGEX_BASE_VENDA);
            Matcher m = p.matcher(base);

            if (!m.matches()) {
                countError++;
            }

        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.baseVenda.type"), "baseVenda");
        } else {
            ticket.setBaseVenda(base);
        }

        return this;
    }

    @Override
    public IValidator checkQtdPax() {

        int countError = 0;
        String qtdPax = ticketDTO.getQtdPax();
        Long value = null;
        if (!Optional.ofNullable(qtdPax).isPresent()) {
            countError++;
        }

        if (countError == 0) {
            Pattern p = Pattern.compile(REGEX_QTD_PAX);
            Matcher m = p.matcher(qtdPax);

            if (!m.matches()) {
                countError++;
            } else {
                value = Long.valueOf(qtdPax);
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.qtdPax.type"), "qtdPax");
        } else {
            ticket.setQtdPax(value);
        }

        return this;
    }

    @Override
    public IValidator checkNumVoo() {

        int countError = 0;

        String numVoo = ticketDTO.getNumVoo();

        if (!Optional.ofNullable(numVoo).isPresent()) {
            countError++;
        }

        if (countError == 0) {
            Pattern p = Pattern.compile(REGEX_NUM_VOO);
            Matcher m = p.matcher(numVoo);

            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.numVoo.type"), "numVoo");
        } else {
            ticket.setNumVoo(numVoo);
        }

        return this;
    }

    @Override
    public IValidator checkAgenciaConsolidada() {

        if (Optional.ofNullable(ticketDTO.getConsolidada()).isPresent()) {
            this.ticket.setConsolidada(ticketDTO.getConsolidada());
        } else {
            this.ticket.setConsolidada("");
        }

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

        if (ticketDTO.getRtOw() != null && (ticketDTO.getRtOw().equals("RT") || ticketDTO.getRtOw().equals("OW"))) {
            message.append("Rt_Ow inválido.\n");
        }

        if (message.length() > 0) {
            this.generateLog(ticketDTO, message.toString(), "rtOw");
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
    public Optional<Ticket> validate(TicketDTO ticketDTO) {
        this.ticketDTO = ticketDTO;
        this.ticket.setErrors(new ArrayList<>());
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

            if (ticketDTO.getLayout().equals(TicketLayoutEnum.FULL)) {
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

            
            this.ticket.setFileId(Long.valueOf(ticketDTO.getFileId()));
            this.ticket.setLayout(TicketLayoutEnum.valueOf(ticketDTO.getLayout()));
            
            return Optional.of(this.ticket);

        } catch (Exception e) {
            Logger.getLogger(com.core.behavior.validator.Validator.class
                    .getName()).log(Level.SEVERE, null, e);
            return Optional.empty();
        }
    }

}
