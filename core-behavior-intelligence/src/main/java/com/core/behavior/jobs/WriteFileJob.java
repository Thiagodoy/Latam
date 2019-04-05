package com.core.behavior.jobs;

import com.core.behavior.model.FileLines;
import com.core.behavior.repository.FileLineRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.services.FileService;
import com.core.behavior.services.LogService;
import com.core.behavior.util.StatusEnum;
import com.core.behavior.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Transactional;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

public class WriteFileJob extends QuartzJobBean {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileLineRepository fileLineRepository;

    @Autowired
    private LogService logService;

    private File fileInput;
    
    private String userId;
    
    private String company;

  

  
    public void run() {
       
    }
    
     @Transactional
    private void saveFile(com.core.behavior.model.File f) {
        f = fileRepository.save(f);
    }

    @Transactional
    private void saveLines(List<FileLines> lines) {
        fileLineRepository.saveAll(lines);
    }

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
       this.fileInput = (File) jec.getJobDetail().getJobDataMap().get("file");
       
        File file = null;
        InputStream stream = null;
        String nameFile = null;
        try {
            file = fileInput;
            nameFile = file.getName();
            stream = new FileInputStream(file);
        } catch (IOException ex) {
            Logger.getLogger(FileService.class.getName()).log(Level.SEVERE, null, ex);
            logService.logGeneric(0, ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }

        Scanner scanner = new Scanner(stream, "UTF-8");

        com.core.behavior.model.File f = new com.core.behavior.model.File();
        f.setCompany("teste");
        f.setName(nameFile);
        f.setUserId("teste");
        f.setStatus(StatusEnum.PROCESSING);
        f.setCreatedDate(LocalDateTime.now());

        try {
            saveFile(f);

            List<FileLines> lines = new ArrayList<>();
            long line = 0;
            while (scanner.hasNext()) {
                String content = scanner.nextLine();
                FileLines flaFileLines = new FileLines(f.getId(), StatusEnum.UPLOADED, content, line++);
                lines.add(flaFileLines);
            }

            saveLines(lines);

            f.setStatus(StatusEnum.UPLOADED);
            fileRepository.save(f);

            file.deleteOnExit();
        } catch (Exception e) {
            logService.logGeneric(f.getId(), e.getMessage());
            f.setStatus(StatusEnum.ERROR);
            fileRepository.save(f);
        }
       
       
    }

}
