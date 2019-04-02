package com.core.behavior.jobs;

import com.core.behavior.dto.Header;
import com.core.behavior.model.File;
import com.core.behavior.model.Ticket;
import com.core.behavior.reader.BeanIoReader;
import com.core.behavior.services.FileService;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.StatusEnum;
import java.util.List;
import java.util.Optional;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class ProcessFileJob extends QuartzJobBean {

    @Autowired
    private FileService fileService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private BeanIoReader reader;

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        List<File> listFiles = fileService.listFilesOfPending();

        listFiles.parallelStream().forEach(f -> {

            Header header = reader.<Header>parse(f, "header", "static/HEADER.xml", true).get(0);

            if (Optional.ofNullable(header).isPresent()) {

                List<Ticket> listTickets = reader.<Ticket>parse(f, "ticket", "static/TICKET.xml", false);

                ticketService.saveAll(listTickets);
            }

        });
        
    }

}
