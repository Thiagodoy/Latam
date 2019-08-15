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
import com.core.behavior.writer.BeanIoWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        PageRequest page = PageRequest.of(0, 5000);

        List<Ticket> tickets = ticketService.listByStatus(TicketStatusEnum.APPROVED, page);

        List<Ticket> shortLayout = tickets.parallelStream().filter(t -> t.getLayout().equals(TicketLayoutEnum.SHORT)).collect(Collectors.toList());

        //List<Ticket> fullLayout = tickets.parallelStream().filter(t -> t.getLayout().equals(TicketLayoutEnum.FULL)).collect(Collectors.toList());

        if (!shortLayout.isEmpty()) {
            this.generateFile(shortLayout, uploadFolder, TicketLayoutEnum.SHORT, Stream.SHORT_LAYOUT_INTEGRATION);
        }

//        if (!fullLayout.isEmpty()) {
//           this.generateFile(shortLayout, uploadFolder, TicketLayoutEnum.FULL, Stream.FULL_LAYOUT_INTEGRATION);
//        }

    }

    private void generateFile(List<Ticket> list, File uploadFolder, TicketLayoutEnum layout, Stream stream) {

        FileIntegrationDTO dTO = mountDTO(list);
        File file = BeanIoWriter.writer(uploadFolder, layout, dTO, stream);

        final String fileName = file.getName();
        boolean isUploaded = this.uploadAndMoveFile(file,generateTagFromLayout(layout));

        if (isUploaded) {            
            list.parallelStream().forEach(t->{            
                t.setFileIntegration(fileName);
                t.setStatus(TicketStatusEnum.WRITED);
                this.ticketService.save(t);
            });            
        } else {
            file.delete();
        }

    }
    
    private Tag generateTagFromLayout(TicketLayoutEnum layout){
        
        if(layout.equals(TicketLayoutEnum.SHORT)){
            return new Tag("model", "20");
        }else{
            return new Tag("model", "50");
        }
        
    }

    private boolean uploadAndMoveFile(File f, Tag tag) {

        try {
            String hash = clientAws.uploadFile(f, Constantes.PATH_INTEGRATION, tag);

            if (Optional.ofNullable(hash).isPresent()) {
                Files.move(f.toPath(), Paths.get(Constantes.DIR_UPLOADED).resolve(f.getName()), StandardCopyOption.REPLACE_EXISTING);
                FileIntegration integration = new FileIntegration(hash, f.getName());
                fileIntegrationRepository.save(integration);
            }
            return true;
        } catch (AmazonS3Exception e) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } catch (Exception ex) {
            Logger.getLogger(IntegrationJob.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
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

}
