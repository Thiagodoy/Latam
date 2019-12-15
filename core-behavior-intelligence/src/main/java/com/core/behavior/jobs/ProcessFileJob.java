package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.io.BeanIoReader;
import com.core.behavior.model.Sequence;
import com.core.behavior.model.TicketError;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.SequenceService;
import com.core.behavior.services.TicketErrorService;
import com.core.behavior.services.TicketService;
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
import java.text.MessageFormat;
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
    private TicketErrorService ticketErrorService;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileProcessStatusService fileProcessStatusService;    

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

    private static final int THREAD_POLL = 50;

    private Map<String, Object> parameters = new HashMap<>();

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public ProcessFileJob(BeanIoReader reader, LogService logService, AgencyService agencyService, FileService fileService,
            FileProcessStatusService fileProcessStatusService, SequenceService sequenceService) {
        this.reader = reader;
        this.logService = logService;
        this.agencyService = agencyService;
        this.fileService = fileService;
        this.fileProcessStatusService = fileProcessStatusService;
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
                });

                fileService.setStage(idFile, StageEnum.VALIDATION_CONTENT.getCode());

                long startValidation = System.currentTimeMillis();
                Map<String, Object> resul = this.runRule1(dto);

                List<Ticket> success = (List<Ticket>) resul.get(LIST_DATA_SUCCESS);
                List<TicketError> error = (List<TicketError>) resul.get(LIST_DATA_ERROR);

                success.parallelStream().forEach(t -> {
                    t.setStatus(TicketStatusEnum.VALIDATION);
                });

                this.writeErrors(error);
                this.generateIds(success);
                this.generateBilheteBehavior(success);
                this.runRules2(success);
                this.runRules3(success);
                this.runRules4(success);

                long timeValidation = (System.currentTimeMillis() - startValidation) / 1000;

                f.setValidationTime(timeValidation);
                f.setRepeatedLine(Integer.valueOf(error.size()).longValue());
                f.setStatus(StatusEnum.VALIDATION_SUCCESS);
                f.setStage(StageEnum.FINISHED.getCode());
                f = fileService.saveFile(f);

                IntegrationJob job = context.getBean(IntegrationJob.class);
                job.setFileId(idFile);

                threadPoolFileIntegration.submit(job);

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

    private void runRules2(List<Ticket> success) {

        long start = System.currentTimeMillis();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POLL);

        success.parallelStream().forEach(t -> {
            executorService.submit(new TicketDuplicityValidationJob(context, t));
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ runRules2 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }

    private void runRules3(List<Ticket> success) {

        long start = System.currentTimeMillis();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POLL);

        success.parallelStream()
                .filter(t -> t.getStatus().equals(TicketStatusEnum.VALIDATION))
                .forEach(t -> {

                    executorService.submit(new TicketCupomValidationJob(context, t));
                });

        executorService.shutdown();
        //Aguarda o termino do processamento
        while (!executorService.isTerminated()) {
        }

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ runRules3 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

    }

    private void runRules4(List<Ticket> success) {

        long start = System.currentTimeMillis();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POLL);

        success.parallelStream().distinct().forEach(t -> {
            executorService.submit(new TicketBilheteBehaviorGroupJob(context, t));
        });

        executorService.shutdown();
        //Aguarda o termino do processamento
        while (!executorService.isTerminated()) {
        }

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

    private void writeErrors(List<TicketError> error) {

        if (!error.isEmpty()) {
            long start = System.currentTimeMillis();
            ticketErrorService.saveBatch(error);
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ writeErrors ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }
    }

    private void generateBilheteBehavior(List<Ticket> tickets) {

        tickets.parallelStream().forEach(t -> {

            try {
                String bilheteBehavior = "";
                String sequencial = "";

                String ano = String.valueOf(t.getDataEmissao().getYear() >= 2020 ? t.getDataEmissao().getYear() - 2020 : t.getDataEmissao().getYear() - 110);
                String mes = StringUtils.leftPad(String.valueOf(t.getDataEmissao().getMonth() + 1), 2, "0");
                String id = String.valueOf(t.getId());
                
                sequencial = (id.length() < SIZE_BILHETE_BEHAVIOR) ? StringUtils.leftPad(id, SIZE_BILHETE_BEHAVIOR, "0") : id.substring(0, SIZE_BILHETE_BEHAVIOR);

                bilheteBehavior = t.getLayout().equals(TicketLayoutEnum.FULL) ? MessageFormat.format("2{0}{1}{2}", ano, mes, sequencial) : MessageFormat.format("1{0}{1}{2}", ano, mes, sequencial);
                t.setBilheteBehavior(bilheteBehavior);
            } catch (Exception e) {
                Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, "[ generateBilheteBehavior ]", e);
            }

        });

    }

}
