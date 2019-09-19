package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.dto.TicketDTO;
import com.core.behavior.dto.TicketDuplicityDTO;
import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.io.BeanIoReader;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.StageEnum;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Stream;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.Utils;
import com.core.behavior.validator.ValidatorFactoryBean;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

    //@Autowired
    //private Validator validator;
    public static final String DATA_USER_ID = "userId";
    public static final String DATA_FILE = "file";
    public static final String DATA_COMPANY = "company";
    public static final String DATA_FILE_ID = "fileId";
    public static final String DATA_LAYOUT_FILE = "layoutFile";

    private final SimpleDateFormat formatter5 = new SimpleDateFormat("ddMMyyyy", new Locale("pt", "BR"));

    private static final int DIAS = 90;

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

                List<Ticket> success = Collections.synchronizedList(new ArrayList<Ticket>());
                List<Log> error = Collections.synchronizedList(new ArrayList<Log>());

                start = System.currentTimeMillis();

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

                Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ REGRAS 1 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

                success.parallelStream().forEach(t -> {
                    t.setStatus(TicketStatusEnum.VALIDATION);
                });

                if (!error.isEmpty()) {
                    start = System.currentTimeMillis();
                    logService.saveBatch(error);
                    Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ PERSIST LOG ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
                }

                //:FIXEME IMPMENETAR FLUXO PARA BILHETES QUE FICARA NO BACKOFFICE
                //List<Ticket> insert = success.stream().filter(t -> t.getStatus().equals(TicketStatusEnum.VALIDATION)).collect(Collectors.toList());

                start = System.currentTimeMillis();
                ticketService.saveBatch(success);
                Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ PERSIST TICKET ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

                List<Ticket> tickets = this.ticketService.listByFileId(idFile);
                this.generateBilheteBehavior(tickets);

                if (!tickets.isEmpty()) {

                    LocalDate s = Utils.dateToLocalDate(new Date()).minusDays(DIAS);
                    LocalDate e = Utils.dateToLocalDate(new Date());

                    final List<TicketDuplicityDTO> listDuplicity = ticketService.listDuplicityByDateEmission(s, e);

                    start = System.currentTimeMillis();

                    tickets.forEach(t -> {
                        Optional<TicketDuplicityDTO> opt = factoryBean.getBean().validate(listDuplicity, t);

                        if (opt.isPresent()) {
                            listDuplicity.add(opt.get());
                        }

                    });
                    Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ REGRAS 2 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
                }
                
                
                //Atualiza os tickets
                start = System.currentTimeMillis();
                tickets.parallelStream().forEach(t->{               
                    t.setStatus(TicketStatusEnum.APPROVED);
                    ticketService.save(t);                    
                });
                
                Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ ATUALIZAR TICKETS 2 ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");
                

                long timeValidation = (System.currentTimeMillis() - startValidation) / 1000;

                fileService.setValidationTime(idFile, timeValidation);

                f.setRepeatedLine(Integer.valueOf(error.size()).longValue());
                f = fileService.saveFile(f);

                fileService.setStatus(idFile, StatusEnum.VALIDATION_SUCCESS);
                fileService.setStage(idFile, StageEnum.FINISHED.getCode());

            } else if (logService.fileHasError(fileId)) {
                fileService.setStatus(idFile, StatusEnum.VALIDATION_ERROR);
            }
        } catch (Throwable e) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, null, e);
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

           // ticketService.save(t);

        });

    }

}
