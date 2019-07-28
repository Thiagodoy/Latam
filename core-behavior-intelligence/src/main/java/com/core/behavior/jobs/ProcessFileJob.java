package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.dto.TicketDTO;
import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.reader.BeanIoReader;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.StageEnum;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Stream;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.validator.ValidatorFactoryBean;
import com.core.behavior.validator.Validator;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
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

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        long start = System.currentTimeMillis();

        File file = (File) jec.getJobDetail().getJobDataMap().get(DATA_FILE);
        String user = jec.getJobDetail().getJobDataMap().getString(DATA_USER_ID);
        Long company = jec.getJobDetail().getJobDataMap().getLong(DATA_COMPANY);
        Long layout = jec.getJobDetail().getJobDataMap().getLong(DATA_LAYOUT_FILE);
        Long fileId = jec.getJobDetail().getJobDataMap().getLong(DATA_FILE_ID);

        com.core.behavior.model.File f = new com.core.behavior.model.File();
        f.setCompany(company);
        f.setId(fileId);
        f.setName(file.getName());
        f.setUserId(user);
        f.setStatus(StatusEnum.VALIDATION_PROCESSING);
        f.setStage(StageEnum.UPLOAD.getCode());
        f.setCreatedDate(LocalDateTime.now());

        f = fileService.saveFile(f);
        final long idFile = f.getId();
        try {

            Stream stream = layout == 1L ? Stream.SHORT_LAYOUT_PARSER : Stream.FULL_LAYOUT_PARSER;
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(file, f, stream);

            fileService.setStage(idFile, StageEnum.VALIDATION_LAYOUT.getCode());

            if (fileParsed.isPresent()) {
                FileParsedDTO dto = fileParsed.get();

                TicketLayoutEnum ticketLayout = TicketLayoutEnum.getLayout(layout);
                dto.getTicket().parallelStream().forEach((t) -> {
                    t.setFileId(String.valueOf(idFile));
                    t.setLayout(ticketLayout.toString());
                });

                long count = 2;
                for (TicketDTO t : dto.getTicket()) {
                    t.setLineFile(String.valueOf(count++));
                }

                fileService.setStage(idFile, StageEnum.VALIDATION_CONTENT.getCode());

                long startValidation = System.currentTimeMillis();

                List<Ticket> out = Collections.synchronizedList(new ArrayList<Ticket>());
                List<Log> error = Collections.synchronizedList(new ArrayList<Log>());

                dto.getTicket().parallelStream().forEach(t -> {
                    Optional<Ticket> op = factoryBean.getBean().validate(t);

                    synchronized (out) {

                        if (op.get().getErrors().isEmpty()) {
                            out.add(op.get());
                        } else {                            
                            error.addAll(op.get().getErrors());
                        }
                    }

                });

                if(!error.isEmpty()){
                    logService.saveBatch(error);
                }
                
                out.parallelStream().forEach(t -> {                    
                    t.setStatus(TicketStatusEnum.APPROVED);                     
                });

                ticketService.saveBatch(out);

                long timeValidation = (System.currentTimeMillis() - startValidation) / 1000;

                fileService.setValidationTime(idFile, timeValidation);

                fileService.setStatus(idFile, !error.isEmpty() ? StatusEnum.VALIDATION_ERROR : StatusEnum.VALIDATION_SUCCESS);
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

}
