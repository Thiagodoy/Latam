package com.core.behavior.services;

import com.core.behavior.dto.LogStatusSinteticoDTO;
import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.repository.LogRepository;
import com.core.behavior.util.Utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.core.behavior.util.Utils.TypeField;
import java.time.LocalDateTime;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private DataSource dataSource;

    @Transactional
    public Log saveLog(Log log) {
        return logRepository.save(log);
    }

    @Transactional
    public void saveAll(List<Log> log) {
        logRepository.saveAll(log);
    }

    @Transactional
    public void logGeneric(long fileId, String message) {
        Log log = new Log();
        log.setMessageError(message);
        log.setFileId(fileId);
        log.setCreatedAt(LocalDateTime.now());
        logRepository.save(log);
    }
    
    public List<Log> listByFileId(Long fileId){
        return logRepository.findByFileId(fileId);
    }
    
    public List<LogStatusSinteticoDTO> listLogSintetico(Long fileId, String fieldName){
        return logRepository.listErroSintetico(fileId, fieldName);
    }
    
    public boolean fileHasError(Long fileId){
        return logRepository.countByFileId(fileId) > 0l;
    }

    public void saveBatch(List<Log> logs) {
      
        try {
            Connection con = dataSource.getConnection();
            List<String> inserts = new ArrayList<>();
            int count = 0;
            for (Log t : logs) {
                inserts.add(Utils.<Log>mountBatchInsert(t, TypeField.LOG));
                count++;
                if (count == 500) {
                    String query = "INSERT INTO `behavior`.`log` VALUES " + inserts.stream().collect(Collectors.joining(","));
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.execute();
                    con.commit();
                    count = 0;
                    inserts.clear();
                }
            }

            if (inserts.size() > 0) {

                String query = "INSERT INTO `behavior`.`log` VALUES " + inserts.stream().collect(Collectors.joining(","));
                PreparedStatement ps = con.prepareStatement(query);
                ps.execute();
                con.commit();

            }

        } catch (SQLException ex) {
            Logger.getLogger(TicketService.class.getName()).log(Level.SEVERE, null, ex);
        }      

    }
}
