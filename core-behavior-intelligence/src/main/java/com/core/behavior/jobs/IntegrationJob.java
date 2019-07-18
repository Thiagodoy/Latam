/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.dto.FileIntegrationDTO;
import com.core.behavior.dto.TicketIntegrationDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.Stream;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.writer.BeanIoWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author thiag
 */
@DisallowConcurrentExecution
public class IntegrationJob extends QuartzJobBean {

    @Autowired
    private TicketService ticketService;

    private static final String DIR_UPLOAD = System.getProperty("user.dir") + "\\upload\\";

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        File uploadFolder = new File(DIR_UPLOAD);

        if (!uploadFolder.isDirectory()) {
            uploadFolder.mkdir();
        }

        PageRequest page = PageRequest.of(0, 5000);

        List<Ticket> tickets = ticketService.listByStatus(TicketStatusEnum.APPROVED, page);

        List<Ticket> shortLayout = tickets.parallelStream().filter(t -> t.getLayout().equals(TicketLayoutEnum.SHORT)).collect(Collectors.toList());

        List<Ticket> fullLayout = tickets.parallelStream().filter(t -> t.getLayout().equals(TicketLayoutEnum.FULL)).collect(Collectors.toList());

        List<File> files = new ArrayList<>();

        if (!shortLayout.isEmpty()) {

            FileIntegrationDTO dTO = mountDTO(shortLayout);
            File file = BeanIoWriter.writer(uploadFolder, TicketLayoutEnum.SHORT, dTO, Stream.SHORT_LAYOUT);

            if (file != null) {
                //:FIXME
                //changeStatus(shortLayout);
                files.add(file);
            }

        }

        if (!fullLayout.isEmpty()) {
            //:TODO Implementar
        }

        //Salvar os arquivos listados
        Arrays.asList(uploadFolder.listFiles()).forEach(f -> {

        });

    }

    private FileIntegrationDTO mountDTO(List<Ticket> list) {
        FileIntegrationDTO dTO = new FileIntegrationDTO();
        List<TicketIntegrationDTO> l = Collections.synchronizedList(new ArrayList<TicketIntegrationDTO>());
        list.parallelStream().forEach(t -> {
            l.add(new TicketIntegrationDTO((t)));
        });

        dTO.setIntegrationDTOs(l);
        
        return dTO;
    }

    private void changeStatus(List<Ticket> list) {
        list.parallelStream().forEach(l -> {
            l.setStatus(TicketStatusEnum.WRITED);
            ticketService.save(l);
        });
    }

}
