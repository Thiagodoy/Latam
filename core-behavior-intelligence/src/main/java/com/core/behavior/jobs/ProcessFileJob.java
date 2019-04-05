package com.core.behavior.jobs;

import com.core.behavior.dto.Header;
import com.core.behavior.model.File;
import com.core.behavior.model.Ticket;
import com.core.behavior.reader.BeanIoReader;
import com.core.behavior.services.FileLinesService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.StatusEnum;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private FileService fileService;

    @Autowired
    private FileLinesService fileLinesService;
    
    @Autowired
    private LogService logService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private BeanIoReader reader;

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

//        List<File> listFiles = fileService.listFilesOfPending();
//
//        listFiles.parallelStream().forEach(f -> {
//            long start = System.currentTimeMillis();
//            try {
//                reader.<Header>parse(f, Constantes.STREAM_HEADER, Constantes.FILE_BEAN_HEADER, true);
//
//                List<Ticket> listTickets = reader.<Ticket>parse(f, Constantes.STREAM_TICKET, Constantes.FILE_BEAN_TICKET, false);
//                ticketService.saveAll(listTickets);
//                fileLinesService.removeLinesProcessed(f.getId());
//
//                final boolean hasErrors = fileLinesService.hasLinesWithErrors(f.getId());
//
//                f.setStatus(hasErrors ? StatusEnum.ERROR : StatusEnum.SUCCESS);
//                
//                f.setLines(f.getLines().stream().filter((l)-> l.getStatus() == null).collect(Collectors.toList()));
//                fileService.update(f);
//
//            } catch (Exception e) {
//                f.setStatus(StatusEnum.ERROR);
//                fileService.update(f);
//                logService.logGeneric(f.getId(),e.getMessage());
//            }
//            
//            long end = System.currentTimeMillis();
//            
//            System.out.println("start -> " + start );
//            System.out.println("end -> " + end );
//            
//        });

    }

}
