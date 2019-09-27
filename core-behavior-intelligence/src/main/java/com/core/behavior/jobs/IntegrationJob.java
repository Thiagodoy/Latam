/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Tag;
import com.core.behavior.aws.client.ClientIntegrationAws;
import com.core.behavior.dto.FileIntegrationDTO;
import com.core.behavior.dto.TicketIntegrationDTO;
import com.core.behavior.model.FileIntegration;
import com.core.behavior.model.Ticket;
import com.core.behavior.repository.FileIntegrationRepository;
import com.core.behavior.services.TicketService;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.Stream;
import com.core.behavior.util.TicketLayoutEnum;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.io.BeanIoWriter;
import com.core.behavior.util.TicketTypeEnum;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Autowired
    private ClientIntegrationAws clientAws;

    @Autowired
    private FileIntegrationRepository fileIntegrationRepository;

    private final int QTD_LOAD = 10000;

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        File uploadFolder = new File(Constantes.DIR_UPLOAD);
        File uploadedFolder = new File(Constantes.DIR_UPLOADED);

        if (!uploadedFolder.isDirectory()) {
            uploadedFolder.mkdir();
        }

        if (!uploadFolder.isDirectory()) {
            uploadFolder.mkdir();
        }

        PageRequest page = PageRequest.of(0, QTD_LOAD);

        List<Ticket> tickets = ticketService.listByStatus(TicketStatusEnum.APPROVED, page);

        List<Ticket> shortLayout = tickets.parallelStream().filter(t -> t.getLayout().equals(TicketLayoutEnum.SHORT)).collect(Collectors.toList());
        List<Ticket> fullLayout = tickets.parallelStream().filter(t -> t.getLayout().equals(TicketLayoutEnum.FULL)).collect(Collectors.toList());

        if (!shortLayout.isEmpty()) {
            List<Ticket> shortLayoutInsert = shortLayout.parallelStream().filter(t -> t.getType().equals(TicketTypeEnum.INSERT)).collect(Collectors.toList());
            List<Ticket> shortLayoutUpdate = shortLayout.parallelStream().filter(t -> t.getType().equals(TicketTypeEnum.UPDATE)).collect(Collectors.toList());

            if (!shortLayoutUpdate.isEmpty()) {
                this.generateFile(shortLayoutUpdate, uploadFolder, TicketLayoutEnum.SHORT, TicketTypeEnum.UPDATE, Stream.SHORT_LAYOUT_INTEGRATION);
            }

            if (!shortLayoutInsert.isEmpty()) {
                this.generateFile(shortLayoutInsert, uploadFolder, TicketLayoutEnum.SHORT, TicketTypeEnum.INSERT, Stream.SHORT_LAYOUT_INTEGRATION);
            }

        }

        if (!fullLayout.isEmpty()) {

            List<Ticket> fullLayoutInsert = fullLayout.parallelStream().filter(t -> t.getType().equals(TicketTypeEnum.INSERT)).collect(Collectors.toList());
            List<Ticket> fullLayoutUpdate = fullLayout.parallelStream().filter(t -> t.getType().equals(TicketTypeEnum.UPDATE)).collect(Collectors.toList());

            if (!fullLayoutUpdate.isEmpty()) {
                this.generateFile(fullLayoutUpdate, uploadFolder, TicketLayoutEnum.FULL, TicketTypeEnum.UPDATE, Stream.FULL_LAYOUT_INTEGRATION);
            }

            if (!fullLayoutInsert.isEmpty()) {
                this.generateFile(fullLayoutInsert, uploadFolder, TicketLayoutEnum.FULL, TicketTypeEnum.INSERT, Stream.FULL_LAYOUT_INTEGRATION);
            }
        }

    }

    private void generateFile(List<Ticket> list, File uploadFolder, TicketLayoutEnum layout, TicketTypeEnum type, Stream stream) {

        FileIntegrationDTO dTO = mountDTO(list);
        File file = BeanIoWriter.writer(uploadFolder, layout, dTO, stream, type);

        final String fileName = file.getName();
        boolean isUploaded = this.uploadAndMoveFile(file, generateTag(layout, type));

        if (isUploaded) {

            long start = System.currentTimeMillis();
            ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);

            list.parallelStream()
                    .forEach(t -> {
                        executorService.submit(() -> {
                            t.setFileIntegration(fileName);
                            t.setStatus(TicketStatusEnum.WRITED);
                            this.ticketService.save(t);
                        });
                    });

            executorService.shutdown();
            //Aguarda o termino do processamento
            while (!executorService.isTerminated()) {
            }

            Logger.getLogger(ProcessFileJob.class.getName()).log(Level.INFO, "[ integração ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

        } else {
            file.delete();
        }

    }

    private List<Tag> generateTag(TicketLayoutEnum layout, TicketTypeEnum type) {

        List<Tag> tags = new ArrayList<>();

        if (layout.equals(TicketLayoutEnum.SHORT)) {
            tags.add(new Tag("model", "20"));
        } else {
            tags.add(new Tag("model", "50"));
        }

        if (type.equals(TicketTypeEnum.INSERT)) {
            tags.add(new Tag("action", "insert"));
        } else {
            tags.add(new Tag("action", "update"));
        }

        return tags;
    }

    private boolean uploadAndMoveFile(File f, List<Tag> tags) {

        try {
            String hash = clientAws.uploadFile(f, Constantes.PATH_INTEGRATION, tags);

            if (Optional.ofNullable(hash).isPresent()) {
                Files.move(f.toPath(), Paths.get(Constantes.DIR_UPLOADED).resolve(f.getName()), StandardCopyOption.REPLACE_EXISTING);
                FileIntegration integration = new FileIntegration(hash, f.getName());
                fileIntegrationRepository.save(integration);
            }
            return true;
        } catch (AmazonS3Exception e) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, "[uploadAndMoveFile]", e);
            return false;
        } catch (Exception ex) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, "[uploadAndMoveFile]", ex);
            return false;
        }
    }

    private FileIntegrationDTO mountDTO(List<Ticket> list) {
        FileIntegrationDTO dTO = new FileIntegrationDTO();
        List<TicketIntegrationDTO> l = Collections.synchronizedList(new ArrayList<TicketIntegrationDTO>());
        list.parallelStream().forEach(t -> {
            
            try {
                l.add(new TicketIntegrationDTO((t)));
            } catch (Exception e) {
                Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, "[mountDTO] id -> " + t.getId(), e);
            }
            
            
        });

        dTO.setIntegrationDTOs(l);

        return dTO;
    }

}
