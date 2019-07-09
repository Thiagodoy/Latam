package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.reader.BeanIoReader;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.validator.ValidatorShortLayout;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
    private TicketService ticketService;

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
        f.setStage(2l);
        f.setCreatedDate(LocalDateTime.now());

        f = fileService.saveFile(f);
        final long idFile = f.getId();
        try {

            String beanTicket = layout == 1L ? Constantes.FILE_BEAN_TICKET_SHORT_LAYOUT : Constantes.FILE_BEAN_TICKET;
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(file, f, Constantes.STREAM_TICKET, beanTicket, user);

            fileService.setStage(idFile, 2);

            if (fileParsed.isPresent()) {
                FileParsedDTO dto = fileParsed.get();

                dto.getTicket().parallelStream().forEach((t) -> {
                    t.setFileId(idFile);
                });

                fileService.setStage(idFile, 3);

                //ticketService.saveBatch(dto.getTicket());
               // List<Ticket> tickets = ticketService.listByFileId(idFile);
                dto.getTicket().forEach(t -> {
                    new ValidatorShortLayout(t,logService,ticketService).validate();
                });

                fileService.setStatus(idFile, ValidatorShortLayout.countErrors > 0 ? StatusEnum.VALIDATION_ERROR : StatusEnum.VALIDATION_SUCCESS);
                fileService.setStage(idFile, 4);
                ValidatorShortLayout.countErrors = 0;
            } else if (logService.fileHasError(fileId)) {
                fileService.setStatus(idFile, StatusEnum.VALIDATION_ERROR);
            }
        } catch (Throwable e) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, null, e);
            logService.logGeneric((f != null ? f.getId() : 0l), e.getLocalizedMessage());
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
