package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.reader.BeanIoReader;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.StatusEnum;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        
        long start = System.currentTimeMillis();
        
        File file = (File) jec.getJobDetail().getJobDataMap().get(DATA_FILE);
        String user = jec.getJobDetail().getJobDataMap().getString(DATA_USER_ID);
        Long company = jec.getJobDetail().getJobDataMap().getLong(DATA_COMPANY);

        com.core.behavior.model.File f = new com.core.behavior.model.File();
        f.setCompany(company);
        f.setName(file.getName());
        f.setUserId(user);
        f.setStatus(StatusEnum.PROCESSING);
        f.setCreatedDate(LocalDateTime.now());

        f = fileService.saveFile(f);
        final long idFile = f.getId();
        try {
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(file, f, Constantes.STREAM_TICKET, Constantes.FILE_BEAN_TICKET, user);

            if (fileParsed.isPresent()) {
                FileParsedDTO dto = fileParsed.get();                

                dto.getTicket().parallelStream().forEach((t) -> {
                    t.setFileId(idFile);
                });

                ticketService.saveBatch(dto.getTicket());               
                fileService.setStatus(idFile,StatusEnum.SUCCESS);
            } else {
               fileService.setStatus(idFile,StatusEnum.ERROR);
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.SEVERE, null, e);
            logService.logGeneric((f != null ? f.getId() : 0l), e.getLocalizedMessage());
            fileService.setStatus(idFile,StatusEnum.ERROR);

        } finally {
            file.delete();
            if (f != null) {
                fileProcessStatusService.generateProcessStatus(f.getId());
            }
            
            long time = (System.currentTimeMillis() - start)/1000;
            fileService.setExecutionTime(idFile, time);
             
        }
    }

}
