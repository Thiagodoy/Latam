package com.core.behavior.services;

import com.core.behavior.model.FileLines;
import com.core.behavior.repository.FileLineRepository;
import com.core.behavior.repository.FileRepository;
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
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public void persistFile(MultipartFile fileInput, String userId, String company) throws IOException {

        File file = Utils.convertToFile(fileInput);

        final String nameFile = file.getName();

        InputStream stream = new FileInputStream(file);
        Scanner scanner = new Scanner(stream, "UTF-8");

        com.core.behavior.model.File f = new com.core.behavior.model.File();
        f.setCompany(company);
        f.setName(nameFile);
        f.setUserId(userId);
        f.setStatus(StatusEnum.UPLOADED);
        f.setCreatedDate(LocalDateTime.now());

        f = fileRepository.save(f);

        List<FileLines> lines = new ArrayList<>();
        long line = 0;
        while (scanner.hasNext()) {
            String content = scanner.nextLine();
            FileLines flaFileLines = new FileLines(f.getId(), StatusEnum.UPLOADED, content,line++);
            lines.add(flaFileLines);            
        }
        
        fileLineRepository.saveAll(lines);
        
        file.deleteOnExit();

    }    
    
    public List<com.core.behavior.model.File> listFilesOfPending(){    
        return fileRepository.findByStatus(StatusEnum.UPLOADED);
    }
    
    public void update(com.core.behavior.model.File file){
        this.fileRepository.save(file);
    }

}
