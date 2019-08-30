package com.core.behavior.validator;

import com.core.behavior.dto.TicketDTO;

import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TypeErrorEnum;
import com.core.behavior.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
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
    private final Ticket ticket = new Ticket();

    private final SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));

    private final static int DATA_VOO_LIMITE_DIAS = 365;
    private final static String REGEX_HORA_VOO = "([01]?[0-9]|2[0-3]):([0-5][0-9]):?([0-5][0-9])?";
    private final static String REGEX_TRECHO = "([A-Z]{3}[/]*)+";
    private final static String REGEX_ORIGEM_DESTINO = "([A-Z]){3}";
    private final static String REGEX_CUPOM = "((^0*[1-9]$)|(^[1-9][0-9]$))";
    private final static String REGEX_BILHETE = "[A-Z0-9]{3,11}";
    private final static String REGEX_TIPO = "(I|N){0,1}";
    private final static String REGEX_CABINE = "[A-Z]{0,1}";
    private final static String REGEX_CIA_VOO = "([A-Z]{1}[0-9]{1})|([A-Z]{1}[A-Z]{1})|([0-9]{1}[A-Z]{1})";
    private final static String REGEX_VALOR_BRL = "^([0-9]{1,3}\\.?)+(,[0-9]{1,2})?$";
    private final static String REGEX_CNPJ = "(^\\d{2}.\\d{3}.\\d{3}/\\d{4}-\\d{2}$)";
    private final static String REGEX_CPF = "(^\\d{3}.\\d{3}.\\d{3}-\\d{2}$)";
    private final static String REGEX_IATA_AGENCIA = "([0-9]{7,8})*";
    private final static String REGEX_BASE_VENDA = "[A-Z]{3}";
    private final static String REGEX_QTD_PAX = "((^0*[1-9]$)|(^[1-9][0-9]$))";
    private final static String REGEX_NUM_VOO = "([0-9]{2,4})|([^A-Z]{2,4})|(^\\d[A-Z]{2,3})|([A-Z]{1,3}\\d$)|(^\\D[0-9]{2,3})|([0-9]{1,3}\\D$)|(\\d\\D){2,4}|(\\D\\d){2,4}";
    private final static String REGEX_BASE_TARIFARIA = "[A-Z0-9]{4,}";
    private final static String REGEX_CLASSE_TARIFARIA = "[A-Z]{0,1}";
    private final static String REGEX_TKT_DESIGNATOR = "[^A-Z0-9]*";
    private final static String REGEX_OND_DIRECIONAL = "[A-Z]{6}";
    private final static String REGEX_RT_OW = "(RT|OW){0,2}";
    private final static String REGEX_PNR_CIA_AGENCIA = "^([0-9]){1,}$";
    private final static String REGEX_SELFBOOKING = "((S|s)elfbooking|(O|o)ffline)";
    private final static String REGEX_TIPO_PAX = "(ADT|CHD|INF){0,3}";
    private final static String REGEX_TIPO_PAGAMENTO = "(Cartão|A Vista|Faturado)";
    private final static String REGEX_CLASSE_SERVICO = "(Econômica Promocional|Econômica|Econômica|Executiva|Economica|Promocional|Econômica Premium|Economy Plus/Comfort|Executiva Promocional|Primeira|Primeira Promocional|((O|o)utras))|[0-9]+";
    private final static String REGEX_NOME_PAX = "[A-Z\\s/]*";
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

    private void generateLog(TicketDTO t, String message, String field) {
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

    private Date parseToDate(String date) {
        try {
            return formatter4.parse(date);
        } catch (Exception e) {
            return null;
        }
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

        if (countError == 0 && dataEmbarque.getTime() < dataEmissao.getTime()) {
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

        if (matcher.matches()) {
            String[] s = ticketDTO.getTrecho().split("/");

            for (String string : s) {

                if (string.length() > 3) {
                    countError++;
                }

            }
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

            String bilhete = MessageFormat.format("{0}{1}{2}", this.ticketDTO.getCodigoAgencia().replaceAll("AG", ""), this.ticketDTO.getBilhete(), this.ticketDTO.getDataEmissao().replaceAll("/", ""));

            ticket.setBilhete(bilhete);
        }

        return this;
    }

    @Override
    public IValidator checkTipoVenda() {

        String tipo = ticketDTO.getTipo();
        int countError = 0;

        if (!Optional.ofNullable(tipo).isPresent() || tipo.length() == 0) {
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

        if (!Optional.ofNullable(cabine).isPresent() || cabine.length() == 0) {
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

        boolean hasConsolidada = Optional.ofNullable(this.ticketDTO.getConsolidada()).isPresent();
        boolean hasEmpresa = Optional.ofNullable(this.ticketDTO.getEmpresa()).isPresent();

        if (!hasEmpresa && hasConsolidada) {
            this.ticket.setEmpresa("");
        }

        if (hasEmpresa) {
            this.ticket.setEmpresa(ticketDTO.getEmpresa());
        }

        if (!hasEmpresa && !hasConsolidada) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.empresa.type"), "empresa");
        }

        if (hasEmpresa && this.ticketDTO.getEmpresa().length() == 0 && hasConsolidada && this.ticketDTO.getConsolidada().length() == 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.empresa.type"), "empresa");
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
        if (!Optional.ofNullable(iataAgencia).isPresent() || iataAgencia.length() == 0) {
            countError++;
        }

        if (countError == 0) {
            Pattern p = Pattern.compile(REGEX_IATA_AGENCIA);
            Matcher m = p.matcher(iataAgencia);

            if (!m.matches()) {
                countError++;
            } else if (iataAgencia.length() > 0) {
                value = Long.valueOf(iataAgencia);
            } else {
                value = null;
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

        boolean hasConsolidada = Optional.ofNullable(this.ticketDTO.getConsolidada()).isPresent();
        boolean hasEmpresa = Optional.ofNullable(this.ticketDTO.getEmpresa()).isPresent();

        if (hasEmpresa && !hasConsolidada) {
            this.ticket.setConsolidada("");
        }

        if (hasConsolidada) {
            this.ticket.setConsolidada(ticketDTO.getConsolidada());
        }

        if (!hasEmpresa && !hasConsolidada) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.consolidada.type"), "consolidada");
        }

        if (hasEmpresa && this.ticketDTO.getEmpresa().length() == 0 && hasConsolidada && this.ticketDTO.getConsolidada().length() == 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.consolidada.type"), "consolidada");
        }

        return this;
    }

    @Override
    public IValidator checkDataExtracao() {

        int countError = 0;

        Date dataExtracao = parseToDate(ticketDTO.getDataExtracao());
        Date dataReserva = parseToDate(ticketDTO.getDataReserva());;
        Date dataEmissao = parseToDate(this.ticketDTO.getDataEmissao());;

        if (!Optional.ofNullable(dataExtracao).isPresent()) {
            countError++;
        }

        if (dataExtracao != null && dataEmissao != null) {
            LocalDateTime emissao = this.dateToLocalDateTime(dataEmissao);
            LocalDateTime extracao = this.dateToLocalDateTime(dataExtracao);

            if (extracao.isBefore(emissao)) {
                countError++;
            }
        }

        if (dataExtracao != null && dataReserva != null) {            
            LocalDateTime extracao = this.dateToLocalDateTime(dataExtracao);
            LocalDateTime reserva = this.dateToLocalDateTime(dataReserva);

            if (extracao.isBefore(reserva)) {
                countError++;
            }
        }

        

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.dataExtracao.type"), "dataExtracao");
        } else {
            ticket.setDataExtracao(dataExtracao);
        }

        return this;

    }

    @Override
    public IValidator checkHoraEmissao() {

        Pattern p = Pattern.compile(REGEX_HORA_VOO);
        int countError = 0;

        if (Optional.ofNullable(ticketDTO.getHoraEmissao()).isPresent() && ticketDTO.getHoraEmissao().length() > 0) {
            try {
                Matcher m = p.matcher(ticketDTO.getHoraEmissao());
                if (!m.matches()) {
                    countError++;
                }
            } catch (Exception e) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.horaEmissao.type"), "horaEmissao");
            } else {
                ticket.setHoraEmissao(ticketDTO.getHoraEmissao());
            }

        } else {
            ticket.setHoraEmissao("");
        }

        return this;
    }

    @Override
    public IValidator checkDataReserva() {

        int countError = 0;

        Date dataReserva = null;

        if (Optional.ofNullable(ticketDTO.getDataReserva()).isPresent() && ticketDTO.getDataReserva().length() > 0) {

            try {
                dataReserva = formatter4.parse(ticketDTO.getDataReserva());
            } catch (ParseException e3) {
                countError++;
            }

            if (countError == 0 && dataReserva != null) {

                try {
                    LocalDateTime emissao = this.dateToLocalDateTime(this.ticket.getDataEmissao());
                    LocalDateTime extracao = this.dateToLocalDateTime(this.ticket.getDataExtracao());
                    LocalDateTime embarque = this.dateToLocalDateTime(this.ticket.getDataEmbarque());
                    LocalDateTime reserva = this.dateToLocalDateTime(dataReserva);

                    if (reserva.isAfter(emissao)) {
                        countError++;
                    }

                    if (reserva.isAfter(embarque)) {
                        countError++;
                    }

                    if (reserva.isAfter(extracao)) {
                        countError++;
                    }
                } catch (Exception e3) {
                    countError++;
                }

            }

            if (countError > 0) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.dataReserva.type"), "dataReserva");
            } else {
                ticket.setDataReserva(dataReserva);
            }

        } else {
            ticket.setDataReserva(null);
        }

        return this;
    }

    @Override
    public IValidator checkHoraReserva() {

        Pattern p = Pattern.compile(REGEX_HORA_VOO);
        int countError = 0;

        if (Optional.ofNullable(ticketDTO.getHoraReserva()).isPresent() && ticketDTO.getHoraReserva().length() > 0) {
            try {
                Matcher m = p.matcher(ticketDTO.getHoraReserva());
                if (!m.matches()) {
                    countError++;
                }
            } catch (Exception e) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.horaReserva.type"), "horaReserva");
            } else {
                ticket.setHoraReserva(ticketDTO.getHoraReserva());
            }

        } else {
            ticket.setHoraReserva("");
        }

        return this;
    }

    @Override
    public IValidator checkHoraPouso() {

        Pattern p = Pattern.compile(REGEX_HORA_VOO);
        int countError = 0;

        if (!Optional.ofNullable(ticketDTO.getHoraPouso()).isPresent()) {
            countError++;
        }

        try {
            Matcher m = p.matcher(ticketDTO.getHoraPouso());
            if (!m.matches()) {
                countError++;
            }
        } catch (Exception e) {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.horaPouso.type"), "horaPouso");
        } else {
            ticket.setHoraPouso(ticketDTO.getHoraPouso());
        }

        return this;
    }

    @Override
    public IValidator checkBaseTarifaria() {

        Pattern p = Pattern.compile(REGEX_BASE_TARIFARIA);
        int countError = 0;

        if (Optional.ofNullable(ticketDTO.getBaseTarifaria()).isPresent() && ticketDTO.getBaseTarifaria().length() > 0) {

            String classeTarifaria = ticketDTO.getClasseTarifa();

            if ((!Optional.ofNullable(classeTarifaria).isPresent() || classeTarifaria.length() == 0)) {
                return this;
            }

            if (!classeTarifaria.equals(ticketDTO.getBaseTarifaria().substring(0, 1))) {
                countError++;
            }

            try {
                Matcher m = p.matcher(ticketDTO.getBaseTarifaria());
                if (!m.matches()) {
                    countError++;
                }
            } catch (Exception e) {
                countError++;
            }

        } else {
            countError++;
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.baseTarifaria.type"), "baseTarifaria");
        } else {
            ticket.setBaseTarifaria(ticketDTO.getBaseTarifaria());
        }

        return this;
    }

    @Override
    public IValidator checkTktDesignator() {

        Pattern p = Pattern.compile(REGEX_TKT_DESIGNATOR);
        int countError = 0;

        if (!Optional.ofNullable(ticketDTO.getTktDesignator()).isPresent()) {
            ticket.setTktDesignator("");
        } else {

            Matcher m = p.matcher(ticketDTO.getTktDesignator());
            if (m.matches()) {
                countError++;
            }

            if (countError > 0) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.tktDesignator.type"), "tktDesignator");
            } else {
                ticket.setTktDesignator(ticketDTO.getTktDesignator());
            }

        }

        return this;
    }

    @Override
    public IValidator checkFamiliaTarifaria() {
        return this;
    }

    @Override
    public IValidator checkClasseTarifa() {

        Pattern p = Pattern.compile(REGEX_CLASSE_TARIFARIA);
        int countError = 0;

        if (!Optional.ofNullable(ticketDTO.getClasseTarifa()).isPresent() || ticketDTO.getClasseTarifa().length() == 0) {
            ++countError;//ticket.setClasseTarifa("");
        }

        if (countError == 0) {
            String base = Optional.ofNullable(ticketDTO.getBaseTarifaria()).isPresent() && ticketDTO.getBaseTarifaria().length() > 0 ? ticketDTO.getBaseTarifaria().substring(0, 1) : "";
            String classe = Optional.ofNullable(ticketDTO.getClasseTarifa()).isPresent() && ticketDTO.getClasseTarifa().length() > 0 ? ticketDTO.getClasseTarifa().substring(0, 1) : ""; //ticketDTO.getClasseTarifa().substring(0, 1);

            if (!base.equals(classe) && classe.length() > 0) {
                countError++;
            }

            Matcher m = p.matcher(classe);
            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.classeTarifa.type"), "classeTarifa");
        } else {
            ticket.setClasseTarifa(ticketDTO.getClasseTarifa());
        }

        return this;
    }

    @Override
    public IValidator checkClasseServico() {

        // Logger.getLogger(com.core.behavior.validator.Validator.class.getName()).log(Level.INFO, this.ticketDTO.getClasseServico());
        if (!Optional.ofNullable(this.ticketDTO.getClasseServico()).isPresent() || this.ticketDTO.getClasseServico().length() == 0) {
            this.ticket.setClasseServico("");
        } else {

            Pattern p = Pattern.compile(REGEX_CLASSE_SERVICO, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(this.ticketDTO.getClasseServico());
            if (!m.matches()) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.classeServico.type"), "classeServico");
            } else {
                this.ticket.setClasseServico(this.ticketDTO.getClasseServico());
            }

        }
        return this;
    }

    @Override
    public IValidator checkOndDirecional() {

        String ondDirecional = ticketDTO.getOndDirecional();
        int countError = 0;

        if (!Optional.ofNullable(ondDirecional).isPresent()) {
            ticket.setOndDirecional("");
        } else {

            Pattern p = Pattern.compile(REGEX_OND_DIRECIONAL);

            Matcher m = p.matcher(ondDirecional);
            if (!m.matches()) {
                countError++;
            }

            if (countError == 0) {
                String origem = ondDirecional.substring(0, 4);
                String destino = ondDirecional.substring(3);

                if (origem.equals(destino)) {
                    countError++;
                }
            }

            if (countError > 0) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.ondDirecional.type"), "ondDirecional");
            } else {
                ticket.setOndDirecional("");
            }

        }

        return this;
    }

    @Override
    public IValidator checkTourCode() {
        if (!Optional.ofNullable(ticketDTO.getTourCode()).isPresent()) {
            ticket.setTourCode("");
        } else {
            ticket.setTourCode(ticketDTO.getTourCode());
        }
        return this;
    }

    @Override
    public IValidator checkRtOw() {

        int countError = 0;
        Pattern p = Pattern.compile(REGEX_RT_OW);

        if (!Optional.ofNullable(ticketDTO.getRtOw()).isPresent() || ticketDTO.getRtOw().length() == 0) {
            countError++;
        }

        if (countError == 0) {
            Matcher m = p.matcher(ticketDTO.getRtOw());
            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.rtOw.type"), "rtOw");
        } else {
            this.ticket.setRtOw(ticketDTO.getRtOw());
        }

        return this;
    }

    @Override
    public IValidator checkValorUs() {

        String tipoVenda = ticketDTO.getTipo();
        boolean hasTipoVenda = Optional.ofNullable(tipoVenda).isPresent();
        boolean hasValorUs = Optional.ofNullable(this.ticketDTO.getValorUs()).isPresent();

        int countError = 0;

        if (hasTipoVenda && tipoVenda.equals("I")) {

            if (!hasValorUs) {
                countError++;
            } else {
                try {

                    Pattern p = Pattern.compile(REGEX_VALOR_BRL);
                    Matcher m = p.matcher(this.ticketDTO.getValorUs());

                    if (!m.matches()) {
                        countError++;
                    } else {
                        Double valor = Double.valueOf(this.ticketDTO.getValorUs().replace(".", "").replace(",", "."));
                        this.ticket.setValorUs(valor);
                    }

                } catch (NumberFormatException e) {
                    countError++;
                }
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.valorUs.type"), "valorUs");
        }

        return this;
    }

    @Override
    public IValidator checkTarifaPublica() {

        int countError = 0;
        String tarifaPublica = ticketDTO.getTarifaPublica();

        if (!Optional.ofNullable(tarifaPublica).isPresent()) {
            countError++;
        } else {
            if (countError == 0) {

                Pattern p = Pattern.compile(REGEX_VALOR_BRL);
                Matcher m = p.matcher(tarifaPublica);

                if (!m.matches()) {
                    countError++;
                }

                if (countError == 0) {
                    try {
                        Double value = Double.valueOf(tarifaPublica.replace(".", "").replace(",", "."));
                        if (value.equals(0.0d)) {
                            countError++;
                        } else {
                            ticket.setTarifaPublica(value);
                        }
                    } catch (NumberFormatException e) {
                        countError++;
                    }

                }

            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.tarifaPublica.type"), "tarifaPublica");
        }

        return this;
    }

    @Override
    public IValidator checkTarifaPublicaUs() {

        int countError = 0;
        String tipoVenda = ticketDTO.getTipo();
        boolean hasTipoVenda = Optional.ofNullable(tipoVenda).isPresent();

        String tarifaPublicUs = ticketDTO.getTarifaPublicUs();
        //tarifaPublicUs

        if (hasTipoVenda && tipoVenda.equals("I")) {

            if (!Optional.ofNullable(tarifaPublicUs).isPresent()) {
                countError++;
            } else {
                try {

                    Pattern p = Pattern.compile(REGEX_VALOR_BRL);
                    Matcher m = p.matcher(tarifaPublicUs);
                    Double valor = Double.valueOf(tarifaPublicUs.replace(".", "").replace(",", "."));

                    if (!m.matches()) {
                        countError++;
                    }

                    if (valor.equals(0.0d)) {
                        countError++;
                    } else {
                        ticket.setTarifaPublicUs(valor);
                    }

                } catch (NumberFormatException e) {
                    countError++;
                }
            }

        } else if (hasTipoVenda && tipoVenda.equals("D")) {
            if (Optional.ofNullable(tarifaPublicUs).isPresent()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.tarifaPublicUs.type"), "tarifaPublicUs");
        }

        return this;
    }

    @Override
    public IValidator checkPnrAgencia() {

        if (!Optional.ofNullable(ticketDTO.getPnrAgencia()).isPresent() || ticketDTO.getPnrAgencia().length() == 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.pnrAgencia.type"), "pnrAgencia");
        } else {
            ticket.setPnrAgencia(ticketDTO.getPnrAgencia());
        }
        return this;
    }

    @Override
    public IValidator checkPnrCiaArea() {

        String pnrCiaAgencia = ticketDTO.getPnrCiaArea();

        if (!Optional.ofNullable(pnrCiaAgencia).isPresent()) {
            this.ticket.setPnrCiaArea("");
        } else {
            Pattern p = Pattern.compile(REGEX_PNR_CIA_AGENCIA);
            Matcher m = p.matcher(pnrCiaAgencia);

            if (m.matches()) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.pnrCiaArea.type"), "pnrCiaArea");
            } else {
                ticket.setPnrAgencia(pnrCiaAgencia);
            }
        }

        return this;
    }

    @Override
    public IValidator checkSelfBookingOffiline() {

        String selfBookingOffiline = ticketDTO.getSelfBookingOffiline();

        if (!Optional.ofNullable(selfBookingOffiline).isPresent() || selfBookingOffiline.length() == 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.selfBookingOffiline.type"), "selfBookingOffiline");
        } else {
            Pattern p = Pattern.compile(REGEX_SELFBOOKING, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(selfBookingOffiline);

            if (!m.matches()) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.selfBookingOffiline.type"), "selfBookingOffiline");
            } else {
                ticket.setSelfBookingOffiline(selfBookingOffiline);
            }
        }

        return this;
    }

    @Override
    public IValidator checkNomePax() {

        Pattern p = Pattern.compile(REGEX_NOME_PAX, Pattern.CASE_INSENSITIVE);

        if (!Optional.ofNullable(ticketDTO.getNomePax()).isPresent()) {
            ticket.setNomePax("");
        } else {

            Matcher m = p.matcher(ticketDTO.getNomePax());

            if (m.matches()) {
                ticket.setNomePax(ticketDTO.getNomePax());
            } else {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.nomePax.type"), "nomePax");
            }
        }

        return this;
    }

    @Override
    public IValidator checkTipoPax() {

        String tipoPax = ticketDTO.getTipoPax();
        if (!Optional.ofNullable(tipoPax).isPresent() || tipoPax.length() == 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.tipoPax.type"), "tipoPax");
        } else {

            Pattern p = Pattern.compile(REGEX_TIPO_PAX);
            Matcher m = p.matcher(tipoPax);

            if (!m.matches()) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.tipoPax.type"), "tipoPax");
            } else {
                ticket.setTipoPax(tipoPax);
            }
        }

        return this;
    }

    @Override
    public IValidator checkCpfPax() {

        String cpfPax = ticketDTO.getCpfPax();
        if (!Optional.ofNullable(cpfPax).isPresent() || cpfPax.length() == 0) {
            ticket.setCpfPax("");
        } else {

            Pattern p = Pattern.compile(REGEX_CPF);
            Matcher m = p.matcher(cpfPax);

            if (!m.matches()) {
                this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.cpfPax.type"), "cpfPax");
            } else {
                ticket.setCpfPax(cpfPax);
            }
        }
        return this;
    }

    @Override
    public IValidator checkEmailPax() {

        if (!Optional.ofNullable(this.ticketDTO.getEmailPax()).isPresent()) {
            this.ticket.setEmailPax("");
        } else {
            this.ticket.setEmailPax(this.ticketDTO.getEmailPax());
        }

        return this;
    }

    @Override
    public IValidator checkCellPax() {

        if (!Optional.ofNullable(this.ticketDTO.getCellPax()).isPresent()) {
            this.ticket.setCellPax("");
        } else {
            this.ticket.setCellPax(this.ticketDTO.getCellPax());
        }

        return this;
    }

    @Override
    public IValidator checkTierFidelidadePax() {
        if (!Optional.ofNullable(this.ticketDTO.getTierFidelidadePax()).isPresent()) {
            this.ticket.setTierFidelidadePax("");
        } else {
            this.ticket.setTierFidelidadePax(this.ticketDTO.getTierFidelidadePax());
        }

        return this;
    }

    @Override
    public IValidator checkTipoPagamento() {

        // Logger.getLogger(com.core.behavior.validator.Validator.class.getName()).log(Level.INFO, ticketDTO.getTipoPagamento());
        String tipoPagamento = ticketDTO.getTipoPagamento();
        int countError = 0;
        if (!Optional.ofNullable(tipoPagamento).isPresent()) {
            countError++;
        } else {
            Pattern p = Pattern.compile(REGEX_TIPO_PAGAMENTO, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(tipoPagamento);

            if (!m.matches()) {
                countError++;
            }
        }

        if (countError > 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.tipoPagamento.type"), "tipoPagamento");
        } else {
            ticket.setTipoPagamento(tipoPagamento);
        }

        return this;
    }

    @Override
    public IValidator checkDigitoVerificador() {
        return this;
    }

    @Override
    public IValidator checkGrupoEmpresa() {
        boolean hasConsolidada = Optional.ofNullable(this.ticketDTO.getGrupoConsolidada()).isPresent();
        boolean hasEmpresa = Optional.ofNullable(this.ticketDTO.getGrupoEmpresa()).isPresent();

        if (!hasEmpresa && hasConsolidada) {
            this.ticket.setGrupoEmpresa("");
        }

        if (hasEmpresa) {
            this.ticket.setGrupoEmpresa(ticketDTO.getGrupoEmpresa());
        }

        if (!hasEmpresa && !hasConsolidada) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.grupoEmpresa.type"), "grupoEmpresa");
        }

        if (hasEmpresa && this.ticketDTO.getGrupoEmpresa().length() == 0 && hasConsolidada && this.ticketDTO.getGrupoConsolidada().length() == 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.grupoEmpresa.type"), "grupoEmpresa");
        }

        return this;
    }

    @Override
    public IValidator checkGrupoConsolida() {

        boolean hasConsolidada = Optional.ofNullable(this.ticketDTO.getGrupoConsolidada()).isPresent();
        boolean hasEmpresa = Optional.ofNullable(this.ticketDTO.getGrupoEmpresa()).isPresent();

        if (hasEmpresa && !hasConsolidada) {
            this.ticket.setGrupoConsolidada("");
        }

        if (hasConsolidada) {
            this.ticket.setGrupoConsolidada(ticketDTO.getGrupoConsolidada());
        }

        if (!hasEmpresa && !hasConsolidada) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.grupoConsolidada.type"), "grupoConsolidada");
        }

        if (hasEmpresa && this.ticketDTO.getGrupoEmpresa().length() == 0 && hasConsolidada && this.ticketDTO.getGrupoConsolidada().length() == 0) {
            this.generateLog(ticketDTO, props.getProperty("fielderror.ticket.grupoConsolidada.type"), "grupoConsolidada");
        }

        return this;

    }

    @Override
    public Optional<Ticket> validate(TicketDTO ticketDTO) {
        this.ticketDTO = ticketDTO;
        this.ticket.setErrors(new ArrayList<>());
        this.ticket.setCodeAgencia(this.ticketDTO.getCodigoAgencia());
        this.ticket.setLineFile(Long.valueOf(this.ticketDTO.getLineFile()));
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

            if (ticketDTO.getLayout().equals(TicketLayoutEnum.FULL.toString())) {
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
