/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.dto.TicketDuplicityDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.util.TicketStatusEnum;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    @Query(nativeQuery = true)
    List<TicketDuplicityDTO>listDuplicityByDateEmission(@Param("start")LocalDate start, @Param("end") LocalDate end);
    
    @Query(nativeQuery = true, value = "select * from ticket t where t.file_id = :fileId ")
    List<Ticket>findByFileId(@Param("fileId")Long fileId);
    
    @Modifying 
    @Transactional
    @Query(value = "delete from ticket  where file_id = :fileId", nativeQuery = true)
    void deleteByFileId(@Param("fileId")Long fileId);
    
    //@Query(nativeQuery = true,countQuery = "select count(*) from ticket where status= :status" ,value = "select * from ticket where status= :status")
    List<Ticket>findByStatus(TicketStatusEnum status, Pageable page);
    
    
}
