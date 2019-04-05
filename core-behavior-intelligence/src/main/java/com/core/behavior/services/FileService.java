package com.core.behavior.services;

import com.core.behavior.jobs.ProcessFileJob2;
import com.core.behavior.model.File;
import com.core.behavior.model.FileLines;
import com.core.behavior.repository.FileLineRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.specifications.FileSpecification;
import com.core.behavior.util.MessageCode;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Utils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private FileLineRepository fileLineRepository;

    @Autowired
    private LogService logService;

    @Autowired
    private SchedulerFactoryBean bean;
    
    
    public File findById(Long id){
        return fileRepository.findById(id).get();
    }

    @Transactional
    public void persistFile(MultipartFile fileInput, String userId, String company) throws IOException, SchedulerException, Exception {

        java.io.File file = Utils.convertToFile(fileInput);
        
        
        if(fileRepository.findByName(file.getName()).isPresent()){
            file.deleteOnExit();
            throw new Exception(MessageCode.FILE_NAME_REPETED.toString());
        }
        
        

        JobDataMap data = new JobDataMap();
        data.put(ProcessFileJob2.DATA_USER_ID, userId);
        data.put(ProcessFileJob2.DATA_COMPANY, company);
        data.put(ProcessFileJob2.DATA_FILE, file);

        JobDetail detail = JobBuilder
                .newJob(ProcessFileJob2.class)
                .withIdentity("WRITE-JOB-" + file.getName())
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
    public com.core.behavior.model.File saveFile(com.core.behavior.model.File f) {
        return fileRepository.save(f);
    }

    @Transactional
    private void saveLines(List<FileLines> lines) {
        fileLineRepository.saveAll(lines);
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
        List<FileLines> linesErrors = fileLineRepository.findByFileIdAndStatus(idFile, StatusEnum.ERROR);
        linesErrors.forEach((line) -> {
            buffer.append(line);
            logService.findByLineId(line.getId()).forEach((log) -> {
                buffer.append(isCsv ? log.toStringCsv() : log.toString());
            });
        });
        return buffer;
    }

    @Transactional
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }
    
    public Page<File>list(String fileName, String userId,String company,LocalDateTime createdAt, Pageable page){
        
         List<Specification<File>> predicates = new ArrayList<>();
        
        if(fileName != null && fileName.length() > 0){
           predicates.add(FileSpecification.fileName(fileName));
        }
        
        if(userId != null && userId.length() > 0){
            predicates.add(FileSpecification.userId(userId));
        }
        
        if(company != null && company.length() > 0){
            predicates.add(FileSpecification.company(company));
        }
        
        if(createdAt != null){
            predicates.add( FileSpecification.dateCreated(createdAt));
        }
        
        Specification<File> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);
        
        return fileRepository.findAll(specification, page);
    }

}
