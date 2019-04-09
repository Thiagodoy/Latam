package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.model.FileLines;
import com.core.behavior.reader.BeanIoReader;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.StatusEnum;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class ProcessFileJob2 extends QuartzJobBean {

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

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        File file = (File) jec.getJobDetail().getJobDataMap().get(DATA_FILE);
        String user = jec.getJobDetail().getJobDataMap().getString(DATA_USER_ID);
        String company = jec.getJobDetail().getJobDataMap().getString(DATA_COMPANY);

        com.core.behavior.model.File f = new com.core.behavior.model.File();
        f.setCompany(company);
        f.setName(file.getName());
        f.setUserId(user);
        f.setStatus(StatusEnum.PROCESSING);
        f.setLines(new ArrayList<FileLines>());
        f.setCreatedDate(LocalDateTime.now());

        f = fileService.saveFile(f);

        try {
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(file, f, Constantes.STREAM_TICKET, Constantes.FILE_BEAN_TICKET, user, company);

            if (fileParsed.isPresent()) {
                FileParsedDTO dto = fileParsed.get();
                f = dto.getFile();

                dto.getTicket().parallelStream().forEach((t) -> {
                    t.setFileId(dto.getFile().getId());
                    try {
                        ticketService.save(t);
                    } catch (Exception e) {
                        System.out.println("Errro -> " + e.getMessage());
                    }
                });

                f.setStatus(StatusEnum.SUCCESS);
                fileService.saveFile(f);
               
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessFileJob2.class.getName()).log(Level.SEVERE, null, e);
            logService.logGeneric((f != null ? f.getId() : 0l), e.getLocalizedMessage());
            f.setStatus(StatusEnum.ERROR);
            fileService.saveFile(f);
           
        } finally {
            file.delete();
            if (f != null) {
                fileProcessStatusService.generateProcessStatus(f.getId());
            }
        }
    }

}
