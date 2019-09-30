package com.core.behavior.services;

import com.core.behavior.dto.LogStatusSinteticoDTO;
import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.jobs.TicketCupomValidationJob;
import com.core.behavior.model.Log;
import com.core.behavior.repository.LogRepository;
import com.core.behavior.util.TicketStatusEnum;
import com.core.behavior.util.Utils;
import java.sql.Connection;
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
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

    
    public List<Log> listByRecordContent(Long fileId,String content) {
        return logRepository.findByRecordContentAndFileId(content,fileId);
    }

    public Stream<Log> listDistinctByFileId(Long fileId) {

        long start = System.currentTimeMillis();

        final List<Log> listLogDistinct = Collections.synchronizedList(new ArrayList<>());

        PageRequest page = PageRequest.of(0, 50000, Sort.by("lineNumber"));

        Page<Log> result = logRepository.findByFileId(fileId, page);

        List<Log> l = result.get().distinct().collect(Collectors.toList());
        listLogDistinct.addAll(l);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        for (int i = 1; i < result.getTotalPages(); i++) {

            PageRequest p = PageRequest.of(i, 10000, Sort.by("lineNumber"));

            executorService.submit(() -> {

                Page<Log> r = logRepository.findByFileId(fileId, p);
                List<Log> ll = r.get().distinct().collect(Collectors.toList());
                listLogDistinct.addAll(ll);

            });

        }

        executorService.shutdown();
        //Aguarda o termino do processamento
        while (!executorService.isTerminated()) {
        }

        Logger.getLogger(LogService.class.getName()).log(Level.INFO, "[ listDistinctByFileId ] -> " + ((System.currentTimeMillis() - start) / 1000) + " sec");

        return listLogDistinct.stream();
    }

    public List<LogStatusSinteticoDTO> listLogSintetico(Long fileId, String fieldName) {
        return logRepository.listErroSintetico(fileId, fieldName);
    }

    public boolean fileHasError(Long fileId) {
        return logRepository.countByFileId(fileId) > 0l;
    }

    public Optional<Log> findByFileIdAndField(String field, Long id) {
        return logRepository.findByFileIdAndFieldName(id, field);
    }

    public void saveBatch(List<Log> logs) {

        try {
            Connection con = dataSource.getConnection();
            List<String> inserts = new ArrayList<>();
            int count = 0;
            for (Log t : logs) {
                inserts.add(Utils.<Log>mountBatchInsert(t, TypeField.LOG));
                count++;
                if (count == 3000) {
                    String query = "INSERT INTO `behavior`.`log` VALUES " + inserts.stream().collect(Collectors.joining(","));
                    Statement ps = con.createStatement();
                    ps.clearBatch();
                    ps.addBatch(query);
                    ps.executeBatch();
                    con.commit();
                    count = 0;
                    inserts.clear();

                }
            }

            if (inserts.size() > 0) {

                String query = "INSERT INTO `behavior`.`log` VALUES " + inserts.stream().collect(Collectors.joining(","));
                Statement ps = con.createStatement();
                ps.clearBatch();
                ps.addBatch(query);
                ps.executeBatch();
                con.commit();
                count = 0;
                inserts.clear();

            }

        } catch (SQLException ex) {
            Logger.getLogger(TicketService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
