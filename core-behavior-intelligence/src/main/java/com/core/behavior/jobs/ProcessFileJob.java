package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.dto.TicketDTO;
import com.core.behavior.model.TicketKey;
import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.io.BeanIoReader;
import com.core.behavior.model.Sequence;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.SequenceService;
import com.core.behavior.services.TicketKeyService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.SequenceTableEnum;
import com.core.behavior.util.StageEnum;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Stream;
import com.core.behavior.util.ThreadPoolFileIntegration;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.TicketTypeEnum;
import com.core.behavior.util.Utils;
import com.core.behavior.validator.ValidatorFactoryBean;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@DisallowConcurrentExecution
public class ProcessFileJob implements Runnable {

    @Autowired
    private BeanIoReader reader;

    @Autowired
    private LogService logService;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileProcessStatusService fileProcessStatusService;

    @Autowired
    private ValidatorFactoryBean factoryBean;

    @Autowired
    private TicketKeyService ticketKeyService;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ThreadPoolFileIntegration threadPoolFileIntegration;

    public static final String DATA_USER_ID = "userId";
    public static final String DATA_FILE = "file";
    public static final String DATA_COMPANY = "company";
    public static final String DATA_FILE_ID = "fileId";
    public static final String DATA_LAYOUT_FILE = "layoutFile";

    public static final String LIST_DATA_SUCCESS = "success";
    public static final String LIST_DATA_ERROR = "error";
    public static final int SIZE_BILHETE_BEHAVIOR = 7;

    private static final int THREAD_POLL = 100;

    private Map<String, Object> parameters = new HashMap<>();

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public ProcessFileJob(BeanIoReader reader, LogService logService, AgencyService agencyService, FileService fileService,
            FileProcessStatusService fileProcessStatusService, ValidatorFactoryBean factoryBean, TicketService ticketService, SequenceService sequenceService) {
        this.reader = reader;
        this.logService = logService;
        this.agencyService = agencyService;
        this.fileService = fileService;
        this.fileProcessStatusService = fileProcessStatusService;
        this.factoryBean = factoryBean;
        // this.ticketService = ticketService;
        this.sequenceService = sequenceService;

    }

    @Override
    public void run() {

        try {
            this.validate();
        } catch (Exception e) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, "[run]", e);
        }

    }

    protected void validate() {

        long start = System.currentTimeMillis();

        File file = (File) this.parameters.get(DATA_FILE);
        Long layout = (Long) this.parameters.get(DATA_LAYOUT_FILE);
        Long fileId = (Long) this.parameters.get(DATA_FILE_ID);

        com.core.behavior.model.File f = fileService.findById(fileId);
        f.setStatus(StatusEnum.VALIDATION_PROCESSING);
        f.setStage(StageEnum.UPLOADED.getCode());

        final String codigoAgencia = agencyService.findById(f.getCompany()).getAgencyCode();

        f = fileService.saveFile(f);
        final long idFile = f.getId();
        try {

            Stream stream = layout == 1L ? Stream.SHORT_LAYOUT_PARSER : Stream.FULL_LAYOUT_PARSER;

            start = System.currentTimeMillis();
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(file, f, stream);
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ PARSER ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

            fileService.setStage(idFile, StageEnum.VALIDATION_LAYOUT.getCode());

            if (fileParsed.isPresent()) {
                FileParsedDTO dto = fileParsed.get();

                TicketLayoutEnum ticketLayout = TicketLayoutEnum.getLayout(layout);
                dto.getTicket().parallelStream().forEach((t) -> {
                    t.setFileId(String.valueOf(idFile));
                    t.setLayout(ticketLayout.toString());
                    t.setCodigoAgencia(codigoAgencia);
                    t.setKey(t.getCodigoAgencia() + t.getDataEmissao());

                });

                List<TicketKey> keys = dto.getTicket()
                        .stream()
                        .distinct()
                        .map(t -> new TicketKey(t.getKey()))
                        .collect(Collectors.toList());

                this.truncateData(keys);

                long count = 2;
                for (TicketDTO t : dto.getTicket()) {
                    t.setLineFile(String.valueOf(count++));
                }

                fileService.setStage(idFile, StageEnum.VALIDATION_CONTENT.getCode());

                long startValidation = System.currentTimeMillis();
                Map<String, Object> resul = this.runRule1(dto);

                List<Ticket> success = (List<Ticket>) resul.get(LIST_DATA_SUCCESS);
                List<Log> error = (List<Log>) resul.get(LIST_DATA_ERROR);

                success.parallelStream().forEach(t -> {
                    t.setStatus(TicketStatusEnum.VALIDATION);
                });

               // this.writeErrors(error);
                this.generateIds(success);
                this.generateBilheteBehavior(success);
                this.runRules2(success);
             //   this.runRules3(success);
               // this.runRules4(success);

//                long timeValidation = (System.currentTimeMillis() - startValidation) / 1000;
//
//                fileService.setValidationTime(idFile, timeValidation);
//
//                f.setRepeatedLine(Integer.valueOf(error.size()).longValue());
//                f = fileService.saveFile(f);
//
//                fileService.setStatus(idFile, StatusEnum.VALIDATION_SUCCESS);
//                fileService.setStage(idFile, StageEnum.FINISHED.getCode());
//
//                IntegrationJob job = context.getBean(IntegrationJob.class);
//                job.setFileId(idFile);
//
//                threadPoolFileIntegration.submit(job);

            } else if (logService.fileHasError(fileId)) {
                fileService.setStatus(idFile, StatusEnum.VALIDATION_ERROR);
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, "[executeInternal] file -> " + fileId, e);
            fileService.setStatus(idFile, StatusEnum.VALIDATION_ERROR);

        } finally {

            Utils.forceDeleteFile(file);

            if (f != null) {
                fileProcessStatusService.generateProcessStatus(f.getId());
            }

            long time = (System.currentTimeMillis() - start) / 1000;
            fileService.setExecutionTime(idFile, time);

        }
    }

    private void truncateData(List<TicketKey> keys) throws SQLException {

        long start = System.currentTimeMillis();

        ticketKeyService.saveBatch(keys);
        ticketKeyService.callProcDeleteTickets();
        
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ truncateData ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
    }

    private Map<String, Object> runRule1(FileParsedDTO dto) {

        Map<String, Object> map = new HashMap<>();
        List<Ticket> success = Collections.synchronizedList(new ArrayList<Ticket>());
        List<Log> error = Collections.synchronizedList(new ArrayList<Log>());

        long start = System.currentTimeMillis();

        dto.getTicket().parallelStream().forEach(t -> {
            Optional<Ticket> op = factoryBean.getBean().validate(t);

            synchronized (success) {
                if (op.isPresent() && op.get().getErrors().isEmpty()) {
                    success.add(op.get());
                } else if (op.isPresent()) {
                    error.addAll(op.get().getErrors());
                }
            }

        });

        map.put(LIST_DATA_SUCCESS, success);
        map.put(LIST_DATA_ERROR, error);
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ runRule1 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

        return map;
    }

    private void runRules2(List<Ticket> success) {

        long start = System.currentTimeMillis();

        CopyOnWriteArrayList<Ticket> storeTicket = new CopyOnWriteArrayList<Ticket>(success);
        List<Ticket>syncList = Collections.synchronizedList(success);

        success.parallelStream().forEach(t -> {

            long countDuplicity = storeTicket
                    .parallelStream()
                    .filter(d -> d.getCupom().equals(t.getCupom()) && d.getAgrupamentoA().equals(t.getAgrupamentoA()))
                    .count();

            long countBackoffice = storeTicket
                    .parallelStream()
                    .filter(d -> d.getAgrupamentoA().equals(t.getAgrupamentoB()) && !d.getAgrupamentoA().equals(t.getAgrupamentoA()))
                    .count();

            if (countDuplicity > 1) {
                t.setStatus(TicketStatusEnum.BACKOFFICE_DUPLICITY);
                t.setBilheteBehavior(null);
            } else if (countBackoffice > 0) {
                t.setStatus(TicketStatusEnum.BACKOFFICE);
                t.setBilheteBehavior(null);
            } else {
                t.setType(TicketTypeEnum.INSERT);
            }

        });

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ runRules2 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }

    private void runRules3(List<Ticket> success) {

        long start = System.currentTimeMillis();

        CopyOnWriteArrayList<Ticket> storeTicket = new CopyOnWriteArrayList<Ticket>(success);

        success.parallelStream().forEach(t -> {
            long sumCupons = storeTicket
                    .stream()
                    .filter(d -> d.getAgrupamentoA().equals(t.getAgrupamentoA()))
                    .mapToLong(Ticket::getCupom)
                    .reduce(0, (a, b) -> a + b);

            long countTickets = storeTicket
                    .stream()
                    .filter(d -> d.getAgrupamentoA().equals(t.getAgrupamentoA()))
                    .count();

            final Double checkValue = countTickets * (((countTickets - 1) * 0.5) + 1);

            boolean isOk = checkValue.longValue() == sumCupons;
            TicketStatusEnum status = isOk ? TicketStatusEnum.APPROVED : TicketStatusEnum.BACKOFFICE_CUPOM;
            t.setStatus(status);
        });

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ runRules3 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }

    private void runRules4(List<Ticket> success) {

        long start = System.currentTimeMillis();

        CopyOnWriteArrayList<Ticket> storeTicket = new CopyOnWriteArrayList<Ticket>(success);

        success.stream().forEach(t -> {

            final List<Ticket> tickets = storeTicket
                    .parallelStream()
                    .filter(d -> d.getAgrupamentoA().equals(t.getAgrupamentoA()))
                    .collect(Collectors.toList());

            Optional<Ticket> first = tickets.parallelStream().min(Comparator.comparing(Ticket::getCupom));

            if (first.isPresent() && tickets.size() > 1) {

                // Certifica que existe bilhete behavior diferente do primeiro cupom
                long count = tickets
                        .parallelStream()
                        .filter(tt -> !tt.getBilheteBehavior().equals(first.get().getBilheteBehavior()))
                        .count();

                if (count > 0) {
                    tickets.parallelStream().filter(tt -> !tt.getBilheteBehavior().equals(first.get().getBilheteBehavior())).forEach(ttt -> {
                        ttt.setBilheteBehavior(first.get().getBilheteBehavior());
                    });
                }
            }

        });

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ runRules4 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }

    private void generateIds(List<Ticket> tickets) throws Exception {

        if (!tickets.isEmpty()) {
            long start = System.currentTimeMillis();

            Sequence sequence = sequenceService.getSequence(SequenceTableEnum.TICKET, tickets.size());

            tickets.parallelStream().forEach(t -> {
                t.setId(sequence.getSequence());
            });

            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ GERANDO IDS ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }

    }

    private void writeErrors(List<Log> error) {

        if (!error.isEmpty()) {
            long start = System.currentTimeMillis();
            logService.saveBatch(error);
            Logger
                    .getLogger(ProcessFileJob.class
                            .getName()).log(Level.INFO, "[ writeErrors ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }
    }

    private void generateBilheteBehavior(List<Ticket> tickets) {

        tickets.parallelStream().forEach(t -> {

            try {
                String bilheteBehavior = "";
                final SimpleDateFormat formmaterDate = new SimpleDateFormat("ddMMyyyy", new Locale("pt", "BR"));
                String dataEmissao = formmaterDate.format(t.getDataEmissao());
                String ano = dataEmissao.substring(dataEmissao.length() - 1);
                String mes = dataEmissao.substring(2, 4);

                String sequencial = "";
                String id = String.valueOf(t.getId());

                if (id.length() < SIZE_BILHETE_BEHAVIOR) {
                    sequencial = StringUtils.leftPad(id, SIZE_BILHETE_BEHAVIOR, "0");
                } else {
                    sequencial = id.substring(0, SIZE_BILHETE_BEHAVIOR);
                }

                bilheteBehavior = t.getLayout().equals(TicketLayoutEnum.FULL) ? MessageFormat.format("2{0}{1}{2}", ano, mes, sequencial) : MessageFormat.format("1{0}{1}{2}", ano, mes, sequencial);

                t.setBilheteBehavior(bilheteBehavior);

            } catch (Exception e) {
                Logger.getLogger(ProcessFileJob.class
                        .getName()).log(Level.SEVERE, "[ generateBilheteBehavior ]", e);
            }

        });

    }

}
