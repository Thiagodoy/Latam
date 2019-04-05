package com.core.behavior.jobs;

import com.core.behavior.dto.FileParsedDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.reader.BeanIoReader;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.StatusEnum;
import java.io.File;
import java.util.List;
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
    private TicketService ticketService;

    public static final String DATA_USER_ID = "userId";
    public static final String DATA_FILE = "file";
    public static final String DATA_COMPANY = "company";

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        File file = (File) jec.getJobDetail().getJobDataMap().get(DATA_FILE);
        String user = jec.getJobDetail().getJobDataMap().getString(DATA_USER_ID);
        String company = jec.getJobDetail().getJobDataMap().getString(DATA_COMPANY);
        com.core.behavior.model.File fileModel = null;
        try {
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(file, Constantes.STREAM_TICKET, Constantes.FILE_BEAN_TICKET, user, company);

            if (fileParsed.isPresent()) {
                FileParsedDTO dto = fileParsed.get();

                dto.getTicket().parallelStream().forEach((t) -> {
                    t.setFileId(dto.getFile().getId());
                });

                List<Ticket>returned = ticketService.saveAll(dto.getTicket());
                System.out.println("returned ->" + returned.size());
                System.out.println("getTicket ->" + dto.getTicket().size());
                fileModel = dto.getFile();
                fileModel.setStatus(StatusEnum.SUCCESS);
                fileService.saveFile(fileModel);
                file.delete();
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessFileJob2.class.getName()).log(Level.SEVERE, null, e);
            logService.logGeneric(fileModel.getId(), e.getLocalizedMessage());
            fileModel.setStatus(StatusEnum.ERROR);
            fileService.saveFile(fileModel);
            file.delete();
        }
    }

}
