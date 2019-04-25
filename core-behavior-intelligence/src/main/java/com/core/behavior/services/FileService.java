package com.core.behavior.services;

import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.model.Agency;
import com.core.behavior.model.File;

import com.core.behavior.repository.FileRepository;
import com.core.behavior.util.MessageCode;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Utils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileProcessStatusService fileProcessStatusService;

    @Autowired
    private LogService logService;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private SchedulerFactoryBean bean;

    @Autowired
    private ClientAws clientAws;

    public File findById(Long id) {
        return fileRepository.findById(id).get();
    }

    
    public java.io.File downloadFile(String fileName, Long companyId) throws IOException{
        
         Agency agency = agencyService.findById(companyId);
         String folder = agency.getS3Path().split("\\\\")[1]; 
        
        return clientAws.downloadFile(fileName, folder);
    }
    
    
    @Transactional
    public void persistFile(MultipartFile fileInput, String userId, Long id, boolean uploadAws, boolean uploadFtp, boolean processFile) throws IOException, SchedulerException, Exception {

        java.io.File file = Utils.convertToFile(fileInput);

        Agency agency = agencyService.findById(id);

        if (uploadAws) {            
            String folder = agency.getS3Path().split("\\")[1];
            clientAws.uploadFile(file, folder);
        }

        if (uploadFtp) {
            clientAws.uploadFile(file, "FRONTUR");
        }

        Optional<File> opt = fileRepository.findByName(file.getName());
        
        
        
        if (opt.isPresent() && !(opt.get().getStatus().equals(StatusEnum.ERROR))) {
            file.delete();
            throw new Exception(MessageCode.FILE_NAME_REPETED.toString());
        }
        
        if(opt.isPresent() && opt.get().getStatus().equals(StatusEnum.ERROR)){
            fileRepository.delete(opt.get());
        }

        if (processFile) {

            JobDataMap data = new JobDataMap();
            data.put(ProcessFileJob.DATA_USER_ID, userId);
            data.put(ProcessFileJob.DATA_COMPANY, id);
            data.put(ProcessFileJob.DATA_FILE, file);

            JobDetail detail = JobBuilder
                    .newJob(ProcessFileJob.class)
                    .withIdentity("WRITE-JOB-" + file.getName(), "process-file")
                    .withDescription("Processing files")
                    .usingJobData(data)
                    .build();

            SimpleTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("trigger-" + file.getName(), "process-file")
                    .startAt(new Date())
                    .withSchedule(simpleSchedule())
                    .build();

            bean.getScheduler().scheduleJob(detail, trigger);
        }

    }

    @Transactional
    public com.core.behavior.model.File saveFile(com.core.behavior.model.File f) {
        return fileRepository.save(f);
    }

    public List<com.core.behavior.model.File> listFilesOfPending() {
        return fileRepository.findByStatus(StatusEnum.UPLOADED);
    }

    @Transactional
    public void update(com.core.behavior.model.File file) {
        this.fileRepository.save(file);
    }

    public StringBuilder generateFileErrors(Long idFile, boolean isCsv) {

        StringBuilder buffer = new StringBuilder();

        logService.listByFileId(idFile).forEach(l -> {
            buffer.append(l);
        });

        return buffer;
    }

    @Transactional
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }

    @Transactional
    public void setStatus(Long fileId, StatusEnum status) {
        File file = fileRepository.findById(fileId).get();
        file.setStatus(status);
        fileRepository.save(file);
    }

    @Transactional
    public void setExecutionTime(Long fileId, Long time) {
        File file = fileRepository.findById(fileId).get();
        file.setExecutionTime(time);
        fileRepository.save(file);
    }

    @Transactional
    public void setParseTime(Long fileId, Long time) {
        File file = fileRepository.findById(fileId).get();
        file.setParseTime(time);
        fileRepository.save(file);
    }

    @Transactional
    public void setPersistTime(Long fileId, Long time) {
        File file = fileRepository.findById(fileId).get();
        file.setPersistTime(time);
        fileRepository.save(file);
    }

    public Page<File> list(String fileName, String userId, String company, LocalDateTime createdAt, Pageable page) {

        List<Specification<File>> predicates = new ArrayList<>();

//        if(fileName != null && fileName.length() > 0){
//           predicates.add(FileSpecification.fileName(fileName));
//        }
//        
//        if(userId != null && userId.length() > 0){
//            predicates.add(FileSpecification.userId(userId));
//        }
//        
//        if(company != null && company.length() > 0){
//            predicates.add(FileSpecification.company(company));
//        }
//        
//        if(createdAt != null){
//            predicates.add( FileSpecification.dateCreated(createdAt));
//        }
        Specification<File> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);
        Page<File> fileResponse = fileRepository.findAll(specification, page);

        fileResponse.getContent().stream().forEach(ff -> {
            ff.setStatusProcess(fileProcessStatusService.getStatusFile(ff.getId()));
        });

        return fileResponse;
    }

}
