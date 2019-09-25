package com.core.behavior.jobs;

import com.core.behavior.comparator.TicketComparator;
import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.dto.TicketDTO;
import com.core.behavior.dto.TicketValidationDTO;
import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.io.BeanIoReader;
import com.core.behavior.model.Sequence;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.SequenceService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.SequenceTableEnum;
import com.core.behavior.util.StageEnum;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Stream;
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
import java.time.LocalDate;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@DisallowConcurrentExecution
public class ProcessFileJob extends QuartzJobBean {

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
    private TicketService ticketService;

    @Autowired
    private SequenceService sequenceService;
    public static final String DATA_USER_ID = "userId";
    public static final String DATA_FILE = "file";
    public static final String DATA_COMPANY = "company";
    public static final String DATA_FILE_ID = "fileId";
    public static final String DATA_LAYOUT_FILE = "layoutFile";

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        long start = System.currentTimeMillis();

        File file = (File) jec.getJobDetail().getJobDataMap().get(DATA_FILE);
        Long layout = jec.getJobDetail().getJobDataMap().getLong(DATA_LAYOUT_FILE);
        Long fileId = jec.getJobDetail().getJobDataMap().getLong(DATA_FILE_ID);

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

                long count = 2;
                for (TicketDTO t : dto.getTicket()) {
                    t.setLineFile(String.valueOf(count++));
                }

                fileService.setStage(idFile, StageEnum.VALIDATION_CONTENT.getCode());

                long startValidation = System.currentTimeMillis();
                Map<String, Object> resul = this.runRule1(dto);

                List<Ticket> success = (List<Ticket>) resul.get("success");
                List<Log> error = (List<Log>) resul.get("error");

                success.parallelStream().forEach(t -> {
                    t.setStatus(TicketStatusEnum.VALIDATION);
                });

                this.writeErrors(error);
                //final List<Ticket> ticketsOld = this.getTicketWrited(codigoAgencia);
                this.generateIds(success);
                this.generateBilheteBehavior(success);
                this.saveTickets(success);
                this.runRules3(success);

                long timeValidation = (System.currentTimeMillis() - startValidation) / 1000;

                fileService.setValidationTime(idFile, timeValidation);

                f.setRepeatedLine(Integer.valueOf(error.size()).longValue());
                f = fileService.saveFile(f);

                fileService.setStatus(idFile, StatusEnum.VALIDATION_SUCCESS);
                fileService.setStage(idFile, StageEnum.FINISHED.getCode());

            } else if (logService.fileHasError(fileId)) {
                fileService.setStatus(idFile, StatusEnum.VALIDATION_ERROR);
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, "[executeInternal]", e);
            fileService.setStatus(idFile, StatusEnum.VALIDATION_ERROR);

        } finally {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException ex) {
                Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (f != null) {
                fileProcessStatusService.generateProcessStatus(f.getId());
            }

            long time = (System.currentTimeMillis() - start) / 1000;
            fileService.setExecutionTime(idFile, time);

        }
    }

    private void saveTickets(List<Ticket> list) throws SQLException {

        long start = System.currentTimeMillis();
        ticketService.saveBatch(list);
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ SALVA TICKETS ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

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

        map.put("success", success);
        map.put("error", error);
        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ REGRAS 1 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

        return map;
    }

    private void runRules3(List<Ticket> success) {

        long start = System.currentTimeMillis();
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

        success.parallelStream().forEach(t -> {
            executorService.submit(new Executor(ticketService,t));
        });

        executorService.shutdown();
        //Aguarda o termino do processamento
        while (!executorService.isTerminated()) {
        }

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ REGRAS 2 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

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

    private List<Ticket> getTicketWrited(String codigoAgencia) {

        long start = System.currentTimeMillis();
        LocalDate e = LocalDate.now().plusDays(1);
        LocalDate s = LocalDate.now().minusDays(90);
        List<Ticket> result = ticketService.listByDateEmission(Utils.localDateToDate(s), Utils.localDateToDate(e), codigoAgencia);

        Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ getTicketWrited ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
        return result;
    }

    private void writeErrors(List<Log> error) {

        if (!error.isEmpty()) {
            long start = System.currentTimeMillis();
            logService.saveBatch(error);
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ PERSIST LOG ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
        }
    }

    private void generateBilheteBehavior(List<Ticket> tickets) {

        tickets.parallelStream().forEach(t -> {
            String bilheteBehavior = "";
            final SimpleDateFormat formmaterDate = new SimpleDateFormat("ddMMyyyy", new Locale("pt", "BR"));
            String dataEmissao = formmaterDate.format(t.getDataEmissao());
            String ano = dataEmissao.substring(dataEmissao.length() - 1);
            String mes = dataEmissao.substring(2, 4);

            String sequencial = "";
            String id = String.valueOf(t.getId());

            if (id.length() < 7) {
                sequencial = StringUtils.leftPad(id, 7, "0");
            } else {
                sequencial = id.substring(0, 7);
            }

            bilheteBehavior = t.getLayout().equals(TicketLayoutEnum.FULL) ? MessageFormat.format("2{0}{1}{2}", ano, mes, sequencial) : MessageFormat.format("1{0}{1}{2}", ano, mes, sequencial);

            t.setBilheteBehavior(bilheteBehavior);

        });

    }

}
