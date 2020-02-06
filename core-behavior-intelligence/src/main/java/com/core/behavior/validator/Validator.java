package com.core.behavior.validator;

import com.core.behavior.dto.TicketDTO;

import com.core.behavior.model.Ticket;
import com.core.behavior.model.TicketError;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.Utils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author thiag
 */
@Data
public class Validator implements IValidator {

    private TicketDTO ticketDTO;
    private TicketError ticketError;
    private final Ticket ticket = new Ticket();

    private final SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
    private final SimpleDateFormat formatter5 = new SimpleDateFormat("ddMMyyyy", new Locale("pt", "BR"));
    private final SimpleDateFormat formatter7 = new SimpleDateFormat("dd/MM/yy", new Locale("pt", "BR"));

    private final static int DATA_VOO_LIMITE_DIAS = 365;
    private final static String REGEX_HORA_VOO = "([01]?[0-9]|2[0-3]):([0-5][0-9]):?([0-5][0-9])?";
    private final static String REGEX_ORIGEM_DESTINO = "([A-Z]){3}";
    private final static String REGEX_CUPOM = "((^0*[1-9]$)|(^[1-9][0-9]$))";
    private final static String REGEX_TIPO = "(I|N){0,1}";
    private final static String REGEX_CABINE = "[A-Z]*";
    private final static String REGEX_CIA_VOO = "([A-Z]{1}[0-9]{1})|([A-Z]{1}[A-Z]{1})|([0-9]{1}[A-Z]{1})";
    private final static String REGEX_VALOR_BRL = "^([0-9]{1,3}\\.?)+(,[0-9]{1,2})?$";
    private final static String REGEX_QTD_PAX = "((^0*[1-9]$)|(^[1-9][0-9]$))";
    private final static String REGEX_NUM_VOO = "([0-9]{2,4})|([^A-Z\\(]{2,4})|(^\\d[A-Z]{2,3})|([A-Z]{1,3}\\d$)|(^\\D[0-9]{2,3})|([0-9]{1,3}\\D$)|(\\d\\D){2,4}|(\\D\\d){2,4}";
    //private final static String REGEX_RT_OW = "(RT|OW){0,2}";
    private final static String REGEX_TIPO_PAX = "(ADT|CHD|INF){0,3}";
    private final static String REGEX_NOME_PAX = "[A-Z\\s/]+";

    public Validator() {
        this.formatter4.setLenient(false);
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
        LocalDate dtEm = null;

        if (!Optional.ofNullable(ticketDTO.getDataEmissao()).isPresent()) {
            countError++;
        }

        if (countError == 0 && !(ticketDTO.getDataEmissao().length() == 10 || ticketDTO.getDataEmissao().length() == 8)) {
            countError++;
        }

        try {
            dataEmbarque = ticketDTO.getDataEmissao().length() == 10 ? formatter4.parse(ticketDTO.getDataEmbarque()) : formatter7.parse(ticketDTO.getDataEmbarque());
            dataEmissao = ticketDTO.getDataEmissao().length() == 10 ? formatter4.parse(ticketDTO.getDataEmissao()) : formatter7.parse(ticketDTO.getDataEmissao());

            LocalDate date2017 = LocalDate.ofYearDay(2017, 1);
            LocalDate date2025 = LocalDate.ofYearDay(2025, 1);
            LocalDate dateEmissao = this.dateToLocalDate(dataEmissao);

            if (dateEmissao.isAfter(date2025) || dateEmissao.isBefore(date2017)) {
                countError++;
            }

            dtEm = this.dateToLocalDate(dataEmissao);

            if (dtEm != null && dtEm.isAfter(LocalDate.now())) {
                countError++;
            }

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
            this.ticketError.activeError("dataEmissao");
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
            /**
             * Solicitante : Mauricelio Data : 05/12/2019 Descrição : Para
             * contemplar datas com layout ddd/MM/yy
             *
             */
            dataEmbarque = ticketDTO.getDataEmbarque().length() == 10 ? formatter4.parse(ticketDTO.getDataEmbarque()) : formatter7.parse(ticketDTO.getDataEmbarque());
            dataEmissao = ticketDTO.getDataEmissao().length() == 10 ? formatter4.parse(ticketDTO.getDataEmissao()) : formatter7.parse(ticketDTO.getDataEmissao());

            LocalDate date2017 = LocalDate.ofYearDay(2017, 1);
            LocalDate date2025 = LocalDate.ofYearDay(2025, 1);
            LocalDate dateEmbarque = this.dateToLocalDate(dataEmbarque);

            if (dateEmbarque.isAfter(date2025) || dateEmbarque.isBefore(date2017)) {
                countError++;
            }

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
            this.ticketError.activeError("dataEmbarque");
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
            this.ticketError.activeError("horaEmbarque");
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
            this.ticketError.activeError("ciaBilhete");
        } else {
            ticket.setCiaBilhete(ticketDTO.getCiaBilhete());
        }

        return this;
    }

    @Override
    public IValidator checkTrechoTkt() {

        String trecho = ticketDTO.getTrecho();
        if (!Optional.ofNullable(trecho).isPresent() || trecho.length() == 0) {
            this.ticket.setTrecho("");
        } else {
            this.ticket.setTrecho(trecho);
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
            this.ticketError.activeError("origem");
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
            this.ticketError.activeError("destino");
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
            this.ticketError.activeError("cupom");
        } else {
            ticket.setCupom(Long.parseLong(ticketDTO.getCupom()));
        }

        return this;
    }

    @Override
    public IValidator checkBilhete() {
        ticket.setBilhete(ticketDTO.getBilhete());
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
            this.ticketError.activeError("tipo");
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
            this.ticketError.activeError("cabine");
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
            this.ticketError.activeError("ciaVoo");
        } else {
            ticket.setCiaVoo(voo);
        }

        return this;
    }

    @Override
    public IValidator checkValorBrl() {

        int countError = 0;
        Double value = null;

        Long cupom = ticket.getCupom();

        String valor = ticketDTO.getValorBrl();
        if (!Optional.ofNullable(valor).isPresent()) {
            countError++;
        } else {
            if (valor.length() == 0 && cupom.equals(1L)) {
                countError++;
            }
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
            this.ticketError.activeError("valorBrl");
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
            this.ticket.setEmpresa(null);
        }

        if (hasEmpresa) {
            this.ticket.setEmpresa(ticketDTO.getEmpresa());
        }

        if (!hasEmpresa && !hasConsolidada) {
            this.ticketError.activeError("empresa");
        }

        if (hasEmpresa && this.ticketDTO.getEmpresa().length() == 0 && hasConsolidada && this.ticketDTO.getConsolidada().length() == 0) {
            this.ticketError.activeError("empresa");
        }

        return this;
    }

    @Override
    public IValidator checkCnpjClienteEmpresa() {

        String cnpj = ticketDTO.getCnpj();

        if (!Optional.ofNullable(cnpj).isPresent()) {
            ticket.setCnpj("");
        } else {
            ticket.setCnpj(cnpj);
        }

        return this;
    }

    @Override
    public IValidator checkIataAgenciaEmissora() {

        String iataAgencia = ticketDTO.getIataAgencia();
        if (!Optional.ofNullable(iataAgencia).isPresent() || iataAgencia.length() == 0) {
            ticket.setIataAgencia(null);
        } else {
            try {
                Long value = null;
                value = Long.valueOf(iataAgencia);
                ticket.setIataAgencia(value);

            } catch (Exception e) {
                ticket.setIataAgencia(null);
            }
        }

        return this;
    }

    @Override
    public IValidator checkBaseVenda() {

        String base = ticketDTO.getBaseVenda();

        if (!Optional.ofNullable(base).isPresent()) {
            ticket.setBaseVenda("");
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
            this.ticketError.activeError("qtdPax");
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
            //Alterar a regex. Ilegivel
            Pattern p = Pattern.compile(REGEX_NUM_VOO);
            Matcher m = p.matcher(numVoo);

            if (!m.matches()) {
                countError++;
            } else {
                try {
                    Long.parseLong(numVoo);
                } catch (Exception e) {
                    countError++;
                }
            }
        }

        if (countError > 0) {
            this.ticketError.activeError("numVoo");
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
            this.ticket.setConsolidada(null);
        }

        if (hasConsolidada && this.ticketDTO.getConsolidada().length() > 0) {
            this.ticket.setConsolidada(ticketDTO.getConsolidada());
        } else {
            this.ticket.setConsolidada(null);
        }

        if (!hasEmpresa && !hasConsolidada) {
            this.ticketError.activeError("consolidada");
        }

        if (hasEmpresa && this.ticketDTO.getEmpresa().length() == 0 && hasConsolidada && this.ticketDTO.getConsolidada().length() == 0) {
            this.ticketError.activeError("consolidada");
        }

        return this;
    }

    @Override
    public IValidator checkDataExtracao() {

        Date dataExtracao = parseToDate(ticketDTO.getDataExtracao());

        if (dataExtracao != null) {
            ticket.setDataExtracao(dataExtracao);
        } else {
            ticket.setDataExtracao(null);
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
                this.ticketError.activeError("horaEmissao");
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
                this.ticketError.activeError("horaReserva");
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

        if (!Optional.ofNullable(ticketDTO.getHoraPouso()).isPresent()) {
            ticket.setHoraPouso(null);
        } else {
            Pattern p = Pattern.compile(REGEX_HORA_VOO);

            try {
                Matcher m = p.matcher(ticketDTO.getHoraPouso());
                if (!m.matches()) {
                    ticket.setHoraPouso(null);
                } else {
                    ticket.setHoraPouso(ticketDTO.getHoraPouso());
                }
            } catch (Exception e) {
                ticket.setHoraPouso(null);
            }

        }

        return this;
    }

    @Override
    public IValidator checkBaseTarifaria() {

        String classeTarifaria = ticketDTO.getClasseTarifa();

        if (Optional.ofNullable(classeTarifaria).isPresent() && classeTarifaria.length() == 0) {
            ticket.setBaseTarifaria("");
        } else if (Optional.ofNullable(classeTarifaria).isPresent() && classeTarifaria.length() > 20) {
            ticket.setBaseTarifaria(classeTarifaria.substring(0, 20));
        } else {
            ticket.setBaseTarifaria(classeTarifaria);
        }

        return this;
    }

    @Override
    public IValidator checkTktDesignator() {

        if (!Optional.ofNullable(ticketDTO.getTktDesignator()).isPresent() || ticketDTO.getTktDesignator().length() == 0) {
            ticket.setTktDesignator("");
        } else {
            ticket.setTktDesignator(ticketDTO.getTktDesignator());
        }

        return this;
    }

    @Override
    public IValidator checkFamiliaTarifaria() {
        return this;
    }

    @Override
    public IValidator checkClasseTarifa() {

        String classeTarifa = ticketDTO.getClasseTarifa();

        if (!Optional.ofNullable(classeTarifa).isPresent() || classeTarifa.length() == 0) {
            ticket.setClasseTarifa("");
        } else {
            ticket.setClasseTarifa(classeTarifa);
        }

        return this;
    }

    @Override
    public IValidator checkClasseServico() {

        if (!Optional.ofNullable(this.ticketDTO.getClasseServico()).isPresent() || this.ticketDTO.getClasseServico().length() == 0) {
            this.ticket.setClasseServico("");
        } else {
            this.ticket.setClasseServico(this.ticketDTO.getClasseServico());
        }
        return this;
    }

    @Override
    public IValidator checkOndDirecional() {

        String ondDirecional = ticketDTO.getOndDirecional();
        if (!Optional.ofNullable(ondDirecional).isPresent() || ondDirecional.length() == 0) {
            ticket.setOndDirecional("");
        } else {
            ticket.setOndDirecional(ondDirecional);
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

        this.ticket.setRtOw("");

        return this;
    }

    @Override
    public IValidator checkValorUs() {

        String tipoVenda = ticketDTO.getTipo();
        Long cupom = ticket.getCupom();
        boolean hasTipoVenda = Optional.ofNullable(tipoVenda).isPresent();
        boolean hasValorUs = Optional.ofNullable(this.ticketDTO.getValorUs()).isPresent();

        int countError = 0;

        if (hasTipoVenda && tipoVenda.equals("I")) {

            if (!hasValorUs && cupom.equals(1L)) {
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
            this.ticketError.activeError("valorUs");
        }

        return this;
    }

    @Override
    public IValidator checkTarifaPublica() {

        String tarifaPublica = ticketDTO.getTarifaPublica();
        if (!Optional.ofNullable(tarifaPublica).isPresent()) {
            ticket.setTarifaPublicUs(0D);
        } else {
            try {

                Pattern p = Pattern.compile(REGEX_VALOR_BRL);
                Matcher m = p.matcher(tarifaPublica);
                Double valor = Double.valueOf(tarifaPublica.replace(".", "").replace(",", "."));
                ticket.setTarifaPublicUs(valor);

            } catch (NumberFormatException e) {
                ticket.setTarifaPublicUs(0D);
            }
        }
        return this;
    }

    @Override
    public IValidator checkTarifaPublicaUs() {

        String tarifaPublicUs = ticketDTO.getTarifaPublicUs();

        if (!Optional.ofNullable(tarifaPublicUs).isPresent()) {
            ticket.setTarifaPublicUs(0D);
        } else {
            try {

                Pattern p = Pattern.compile(REGEX_VALOR_BRL);
                Matcher m = p.matcher(tarifaPublicUs);
                Double valor = Double.valueOf(tarifaPublicUs.replace(".", "").replace(",", "."));
                ticket.setTarifaPublicUs(valor);

            } catch (NumberFormatException e) {
                ticket.setTarifaPublicUs(0D);
            }
        }
        return this;
    }

    @Override
    public IValidator checkPnrAgencia() {

        if (!Optional.ofNullable(ticketDTO.getPnrAgencia()).isPresent() || ticketDTO.getPnrAgencia().length() == 0) {
            this.ticketError.activeError("pnrAgencia");
        } else {
            
            
            if(ticketDTO.getPnrAgencia().length()<= 20){
                ticket.setPnrAgencia(ticketDTO.getPnrAgencia());
            }else{
                ticket.setPnrAgencia(ticketDTO.getPnrAgencia().substring(0, 20));
            }
            
        }
        return this;
    }

    @Override
    public IValidator checkPnrCiaArea() {

        String pnrCiaAgencia = ticketDTO.getPnrCiaArea();

        if (!Optional.ofNullable(pnrCiaAgencia).isPresent()) {
            this.ticketError.activeError("pnrCiaArea");
        } else {
            
            if(pnrCiaAgencia.length() <= 10){
                ticket.setPnrCiaArea(pnrCiaAgencia);
            }else{
                ticket.setPnrCiaArea(pnrCiaAgencia.substring(0, 10));
            }
            
        }

        return this;
    }

    @Override
    public IValidator checkSelfBookingOffiline() {

        String selfBookingOffiline = ticketDTO.getSelfBookingOffiline();

        if (!Optional.ofNullable(selfBookingOffiline).isPresent()) {
            ticket.setSelfBookingOffiline("");
        } else {
            ticket.setSelfBookingOffiline(selfBookingOffiline);
        }

        return this;
    }

    @Override
    public IValidator checkNomePax() {

        Pattern p = Pattern.compile(REGEX_NOME_PAX, Pattern.CASE_INSENSITIVE);

        if (!Optional.ofNullable(ticketDTO.getNomePax()).isPresent()) {
            ticket.setNomePax("NO NAME");
        } else {
            ticket.setNomePax(ticketDTO.getNomePax());
        }

        return this;
    }

    @Override
    public IValidator checkTipoPax() {

        String tipoPax = ticketDTO.getTipoPax();
        if (!Optional.ofNullable(tipoPax).isPresent() || tipoPax.length() == 0) {
            this.ticketError.activeError("tipoPax");
        } else {

            Pattern p = Pattern.compile(REGEX_TIPO_PAX);
            Matcher m = p.matcher(tipoPax);

            if (!m.matches()) {
                this.ticketError.activeError("tipoPax");
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
            ticket.setCpfPax(cpfPax);
        }
        return this;
    }

    @Override
    public IValidator checkEmailPax() {

        if (!Optional.ofNullable(this.ticketDTO.getEmailPax()).isPresent()) {
            this.ticket.setEmailPax("");
        } else {
            if (this.ticketDTO.getEmailPax().length() <= 40) {
                this.ticket.setEmailPax(this.ticketDTO.getEmailPax());
            } else {
                this.ticket.setEmailPax(this.ticketDTO.getEmailPax().substring(0, 40));
            }

        }

        return this;
    }

    @Override
    public IValidator checkCellPax() {

        if (!Optional.ofNullable(this.ticketDTO.getCellPax()).isPresent()) {
            this.ticket.setCellPax("");
        } else {
            
            if(this.ticketDTO.getCellPax().length() <= 20){
                this.ticket.setCellPax(this.ticketDTO.getCellPax());    
            }else{
                this.ticket.setCellPax(this.ticketDTO.getCellPax().substring(0, 20));    
            }
            
            
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

        String tipoPagamento = ticketDTO.getTipoPagamento();
        int countError = 0;
        if (!Optional.ofNullable(tipoPagamento).isPresent()) {
            countError++;
        }

        if (countError > 0) {
            this.ticketError.activeError("tipoPagamento");
        } else {
            if(tipoPagamento.length() <= 30){
                ticket.setTipoPagamento(tipoPagamento);
            }else{
                ticket.setTipoPagamento(tipoPagamento.substring(0, 30));
            }
            
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
            this.ticketError.activeError("grupoEmpresa");
        }

        if (hasEmpresa && this.ticketDTO.getGrupoEmpresa().length() == 0 && hasConsolidada && this.ticketDTO.getGrupoConsolidada().length() == 0) {
            this.ticketError.activeError("grupoEmpresa");
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
            this.ticketError.activeError("grupoConsolidada");
        }

        if (hasEmpresa && this.ticketDTO.getGrupoEmpresa().length() == 0 && hasConsolidada && this.ticketDTO.getGrupoConsolidada().length() == 0) {
            this.ticketError.activeError("grupoConsolidada");
        }

        return this;

    }

    private void generateAgrupamentoA() {

        String codigoAgencia = this.ticket.getCodeAgencia().replaceAll("AG", "");
        String dataEmissao = formatter5.format(this.ticket.getDataEmissao());
        String nomePassageiro = "";

        if (this.ticket.getNomePax().length() >= 17) {
            nomePassageiro = ticket.getNomePax().substring(0, 16);
        } else if (this.ticket.getNomePax().length() < 17 && this.ticket.getNomePax().length() > 0) {
            nomePassageiro = ticket.getNomePax().substring(0, this.ticket.getNomePax().length() - 1);
        }

        //Correção referente a inversão da barra que estava aplicando o scape do Mysql
        nomePassageiro = nomePassageiro.replaceAll("\\\\", " ");

        String agrupamento = MessageFormat.format("{0}{1}{2}{3}", codigoAgencia, this.ticket.getPnrAgencia(), dataEmissao, nomePassageiro);
        ticket.setAgrupamentoA(agrupamento);
    }

    private void generateAgrupamentoB() {

        String codigoAgencia = this.ticket.getCodeAgencia().replaceAll("AG", "");
        String dataEmissao = formatter5.format(this.ticket.getDataEmissao());

        String agrupamento = MessageFormat.format("{0}{1}{2}", codigoAgencia, this.ticket.getPnrAgencia(), dataEmissao);
        ticket.setAgrupamentoB(agrupamento);
    }

    private void generateAgrupamentoC() {

        String codigoAgencia = this.ticket.getCodeAgencia().replaceAll("AG", "");
        String dataEmissao = formatter5.format(this.ticket.getDataEmissao());
        String bilhete = this.ticket.getBilhete() != null ? this.ticket.getBilhete().substring(0, 5) : this.generateMockBilhete();

        String bi = StringUtils.leftPad(bilhete, 5, "0");

        String agrupamento = MessageFormat.format("{0}1{1}{2}", codigoAgencia, bi, dataEmissao);

        ticket.setAgrupamentoC(agrupamento);
    }

    private String generateMockBilhete() {
        long value = (long) ((Math.random() * 99999) + 1);

        return String.valueOf(value);
    }

    private void generateNameClient() {

        Optional<String> consolida = Optional.ofNullable(this.ticket.getConsolidada());
        Optional<String> empresa = Optional.ofNullable(this.ticket.getEmpresa());
        String nomeEmpresa = "";

        if ((consolida.isPresent() && empresa.isPresent()) || consolida.isPresent()) {
            nomeEmpresa = MessageFormat.format("{0} - {1}", ticketDTO.getCodigoAgencia(), consolida.get());
        } else if (empresa.isPresent()) {
            nomeEmpresa = MessageFormat.format("{0} - {1}", ticketDTO.getCodigoAgencia(), empresa.get());
        } else {
            nomeEmpresa = ticketDTO.getCodigoAgencia();
        }

        this.ticket.setNomeCliente(nomeEmpresa);
    }

    @Override
    public void validate(TicketDTO ticketDTO) {
        this.ticketDTO = ticketDTO;
        this.ticketError = new TicketError(Long.valueOf(this.ticketDTO.getFileId()), Long.valueOf(this.ticketDTO.getLineFile()), this.ticketDTO.toString());

        this.ticket.setCodeAgencia(this.ticketDTO.getCodigoAgencia());
        this.ticket.setLineFile(Long.valueOf(this.ticketDTO.getLineFile()));
        this.ticket.setKey(ticketDTO.getKey());

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
                    checkCiaVoo(). //Para Alterar remover a validação
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
                        //.checkDataReserva()
                        .checkHoraReserva()
                        .checkHoraPouso()
                        .checkBaseTarifaria() //Cancelado a pedido do Mauricelio 22/09/2019
                        .checkTktDesignator()
                        .checkFamiliaTarifaria()
                        //.checkClasseTarifa() Cancelado a pedido do Mauricelio 22/09/2019
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

            //Geração das chaves
            if (!this.ticketError.hasError()) {
                //Gera os agrupamentos
                if (ticket.getLayout().equals(TicketLayoutEnum.FULL)) {
                    this.generateAgrupamentoA();
                    this.generateAgrupamentoB();
                } else {
                    this.generateAgrupamentoC();
                }

                this.generateNameClient();
            }

        } catch (Exception e) {
            Logger.getLogger(com.core.behavior.validator.Validator.class
                    .getName()).log(Level.SEVERE, "[ validate ]", e);
        }
    }

    public LocalDate dateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
