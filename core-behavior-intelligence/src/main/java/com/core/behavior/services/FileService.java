package com.core.behavior.services;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.dto.FileStatusProcessDTO;
import com.core.behavior.exception.ActivitiException;
import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.model.Agency;
import com.core.behavior.model.File;

import com.core.behavior.repository.FileRepository;
import com.core.behavior.sftp.ClientSftp;
import com.core.behavior.specifications.FileSpecification;
import com.core.behavior.util.MessageCode;

import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Utils;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private ClientSftp clientSftp;

    @Autowired
    private FileService fileService;

    public File findById(Long id) {
        return fileRepository.findById(id).get();
    }

    public java.io.File downloadFile(String fileName, Long companyId) throws IOException {

        Agency agency = agencyService.findById(companyId);
        String folder = agency.getS3Path().split("\\\\")[1];

        return clientAws.downloadFile(fileName, folder);
    }

    public void persistFile(MultipartFile fileInput, String userId, Long id, boolean uploadAws, boolean uploadFtp, boolean processFile) throws IOException, SchedulerException {

        java.io.File file = Utils.convertToFile(fileInput);

        Agency agency = agencyService.findById(id);
        String folder = agency.getS3Path().split("\\\\")[1];
        Long layout = agency.getLayoutFile();

        if (uploadAws || uploadFtp) {
            this.persist(userId, id, file, StatusEnum.COLLECTOR_UPLOADED,0);
            this.uploadFile(uploadAws, uploadFtp, folder, file);
        } else if (processFile) {

            String[] s = new String[5];
            s[0] = "VALIDATION_UPLOADED";
            s[1] = "VALIDATION_PROCESSING";
            s[2] = "VALIDATION_PARSE";
            s[3] = "VALIDATION_ERROR";
            s[4] = "VALIDATION_SUCCESS";

            Page<File> result = this.list(file.getName(), null, new Long[]{id}, null, PageRequest.of(0, 10, Sort.by("createdDate").ascending()), s, null, null);

            if (!result.getContent().isEmpty()) {
                throw new ActivitiException(MessageCode.FILE_NAME_REPETED);
            }

            com.core.behavior.model.File f = this.persist(userId, id, file, StatusEnum.COLLECTOR_UPLOADED,1);
            this.processFile(userId, id, file, layout, f.getId());
        }

    }

    private void uploadFile(boolean uploadAws, boolean uploadFtp, String folder, java.io.File file) throws IOException {

        if (uploadAws) {
            try {
                clientAws.uploadFile(file, folder);
            } catch (AmazonS3Exception e) {
                FileUtils.forceDelete(file); 
                throw new ActivitiException(MessageCode.SERVER_ERROR_AWS);
            }
        }

        if (uploadFtp) {
            try {
                clientSftp.uploadFile(file, folder);
            } catch (Exception e) {            
                FileUtils.forceDelete(file);            
                throw new ActivitiException(MessageCode.SERVER_ERROR_SFTP);
            }
        }

    }

    private void processFile(String userId, Long id, java.io.File file, Long layout, Long fileId) throws SchedulerException {

        JobDataMap data = new JobDataMap();
        data.put(ProcessFileJob.DATA_USER_ID, userId);
        data.put(ProcessFileJob.DATA_COMPANY, id);
        data.put(ProcessFileJob.DATA_FILE, file);
        data.put(ProcessFileJob.DATA_FILE_ID, fileId);
        data.put(ProcessFileJob.DATA_LAYOUT_FILE, layout);

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

    @Transactional
    private com.core.behavior.model.File persist(String userId, Long id, java.io.File file, StatusEnum status,long stage) throws IOException {
        com.core.behavior.model.File f = new com.core.behavior.model.File();
        f.setCompany(id);
        f.setName(file.getName());
        f.setUserId(userId);
        f.setStatus(status);
        f.setStage(stage);        
        f.setCreatedDate(LocalDateTime.now());

        try {
            f = fileService.saveFile(f);
            return f;
        } catch (DataIntegrityViolationException e) {
             FileUtils.forceDelete(file); 
            throw new ActivitiException(MessageCode.FILE_NAME_REPETED);
        }
    }

    @Transactional
    public com.core.behavior.model.File saveFile(com.core.behavior.model.File f) {
        return fileRepository.save(f);
    }

    public List<com.core.behavior.model.File> listFilesOfPending() {
        return fileRepository.findByStatus(StatusEnum.COLLECTOR_UPLOADED);
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
    public void setStage(Long fileId, long s) {
        File file = fileRepository.findById(fileId).get();
        file.setStage(s);
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

    public Page<File> list(String fileName, String userId, Long[] company, LocalDateTime createdAt, Pageable page, String[] status, Long start, Long end) {

        List<Specification<File>> predicates = new ArrayList<>();

        if (Optional.ofNullable(fileName).isPresent()) {
            predicates.add(FileSpecification.fileName(fileName));
        }

        if (company != null) {
            predicates.add(FileSpecification.company(Arrays.asList(company)));
        }

        if (status != null) {
            predicates.add(FileSpecification.status(Arrays.asList(status)));
        }

        if (start != null && end != null) {
            LocalDateTime lstart = LocalDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault());
            LocalDateTime lend = LocalDateTime.ofInstant(Instant.ofEpochMilli(end), ZoneId.systemDefault());
            predicates.add(FileSpecification.dateBetweem(lstart, lend));
        }

        Specification<File> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);
        Page<File> fileResponse = fileRepository.findAll(specification, page);

        fileResponse.getContent().parallelStream().forEach(ff -> {
            ff.setStatusProcess(fileProcessStatusService.getStatusFile(ff.getId()));
        });

        return fileResponse;
    }
    
    
    public List<FileStatusProcessDTO>statusFilesProcess(Long id){
       return  this.fileRepository.statusProcesss(id);
    }

}
