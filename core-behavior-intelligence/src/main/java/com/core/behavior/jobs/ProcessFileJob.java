package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.dto.TicketDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.io.BeanIoReader;
import com.core.behavior.model.Sequence;
import com.core.behavior.model.TicketError;
import com.core.behavior.model.TicketKey;
import com.core.behavior.model.TicketStage;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.SequenceService;
import com.core.behavior.services.TicketErrorService;
import com.core.behavior.services.TicketKeyService;
import com.core.behavior.services.TicketService;
import com.core.behavior.services.TicketStageService;
import com.core.behavior.util.SequenceTableEnum;
import com.core.behavior.util.StageEnum;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Stream;
import com.core.behavior.util.ThreadPoolFileIntegration;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.Utils;
import com.core.behavior.validator.Validator;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class ProcessFileJob implements Runnable {

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
    private TicketService ticketService;

    @Autowired
    private TicketStageService ticketStageService;

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

    @Autowired
    private TicketErrorService ticketErrorService;

    private static final int THREAD_POLL = 100;

    private Map<String, Object> parameters = new HashMap<>();

    @Autowired
    private TicketKeyService ticketKeyService;

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public ProcessFileJob(BeanIoReader reader, LogService logService, AgencyService agencyService, FileService fileService,
            FileProcessStatusService fileProcessStatusService, TicketService ticketService, SequenceService sequenceService) {
        this.reader = reader;
        this.logService = logService;
        this.agencyService = agencyService;
        this.fileService = fileService;
        this.fileProcessStatusService = fileProcessStatusService;
        this.ticketService = ticketService;
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
                List<TicketError> error = (List<TicketError>) resul.get(LIST_DATA_ERROR);

                boolean integra = success.size() > 0;
                success.parallelStream().forEach(t -> {
                    t.setStatus(TicketStatusEnum.VALIDATION);
                });

                this.writeErrors(error);
                this.generateIds(success);

                this.checkDuplicity(success);
                this.checkCupon(success);
                this.checkBilheteBehavior(success);

                this.saveTickets(success);

                long timeValidation = (System.currentTimeMillis() - startValidation) / 1000;

                fileService.setValidationTime(idFile, timeValidation);

                f.setRepeatedLine(Integer.valueOf(error.size()).longValue());
                f = fileService.saveFile(f);

                fileService.setStatus(idFile, StatusEnum.VALIDATION_SUCCESS);
                fileService.setStage(idFile, StageEnum.FINISHED.getCode());

                if (integra) {
                    IntegrationJob job = context.getBean(IntegrationJob.class);
                    job.setFileId(idFile);
                    threadPoolFileIntegration.submit(job);
                }

                System.gc();

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

    private void saveTickets(List<Ticket> success) throws SQLException {
        long start = System.currentTimeMillis();
        ticketService.saveBatch(success);
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ saveTickets ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
    }

    private void truncateData(List<TicketKey> keys) throws SQLException {

        long start = System.currentTimeMillis();

        ticketKeyService.saveBatch(keys);
        ticketKeyService.callProcDeleteTickets();

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ truncateData ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
    }

    private void mountStage(List<Ticket> success) throws SQLException {

        long start = System.currentTimeMillis();

        List<TicketStage> stage = success
                .stream()
                .parallel()
                .map(s -> new TicketStage(null, s.getCupom(), s.getAgrupamentoA(), s.getAgrupamentoB(), s.getBilheteBehavior(), LocalDateTime.now()))
                .collect(Collectors.toList());

        this.ticketStageService.saveBatch(stage);
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ mountStage ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
    }

    private Map<String, Object> runRule1(FileParsedDTO dto) {

        Map<String, Object> map = new HashMap<>();
        List<Ticket> success = Collections.synchronizedList(new ArrayList<Ticket>());
        List<TicketError> error = Collections.synchronizedList(new ArrayList<TicketError>());

        long start = System.currentTimeMillis();

        dto.getTicket().parallelStream().forEach(t -> {
            final Validator validator = new Validator();
            validator.validate(t);

            if (!validator.getTicketError().hasError()) {
                success.add(validator.getTicket());
            } else if (validator.getTicketError().hasError()) {
                error.add(validator.getTicketError());
            }

        });

        map.put(LIST_DATA_SUCCESS, success);
        map.put(LIST_DATA_ERROR, error);
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ runRule1 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

        return map;
    }

    private void checkDuplicity(List<Ticket> success) {

        Map<String, List<Ticket>> map = success.parallelStream().collect(Collectors.groupingBy(Ticket::getAgrupamentoA));

        long start = System.currentTimeMillis();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POLL);

        map.keySet().parallelStream().forEach(key -> {
            synchronized (executorService) {
                executorService.submit(new TicketDuplicityValidationJob(map.get(key)));
            }
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ checkDuplicity ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }

    private void checkCupon(List<Ticket> success) {

        Map<String, List<Ticket>> map = success.parallelStream().collect(Collectors.groupingBy(Ticket::getAgrupamentoA));

        long start = System.currentTimeMillis();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POLL);

        map.keySet().parallelStream().forEach(key -> {
            synchronized (executorService) {
                List<Ticket> tickets = map.get(key)
                        .stream()
                        .filter(t -> t.getStatus().equals(TicketStatusEnum.VALIDATION))
                        .collect(Collectors.toList());

                executorService.submit(new TicketCupomValidationJob(tickets));
            }
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ checkCupon ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
    }

    private void checkBilheteBehavior(List<Ticket> success) {

        Map<String, List<Ticket>> map = success.parallelStream().collect(Collectors.groupingBy(Ticket::getAgrupamentoA));

        long start = System.currentTimeMillis();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POLL);

        map.keySet().parallelStream().forEach(key -> {
            synchronized (executorService) {
                List<Ticket> tickets = map.get(key)
                        .stream()
                        .filter(t -> t.getStatus().equals(TicketStatusEnum.APPROVED))
                        .collect(Collectors.toList());

                executorService.submit(new TicketBilheteBehaviorGroupJob(tickets));
            }
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ checkCupon ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

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

    private void writeErrors(List<TicketError> error) {

        if (!error.isEmpty()) {
            long start = System.currentTimeMillis();
            ticketErrorService.saveBatch(error);
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ writeErrors ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }
    }

}
