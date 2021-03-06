/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.dto.TicketValidationDTO;
import com.core.behavior.model.Ticket;
import com.core.behavior.util.TicketStatusEnum;
import java.time.LocalDate;
import java.util.Date;
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
    

    @Query(nativeQuery = true, value = "select * from ticket t where t.cupom = :cupom and t.agrupamento_a = :agrupa")
    List<Ticket> findToUpdate(@Param("agrupa") String agrupamentoA, @Param("cupom") Long cupom);

    @Query(nativeQuery = true, value = "select * from ticket t where t.file_id = :fileId ")
    List<Ticket> findByFileId(@Param("fileId") Long fileId);

    @Query(nativeQuery = true, value = "select  sum(cupom) = truncate(count(1)*(((count(1)-1) * 0.5)+1),0)  from  behavior.ticket_stage where agrupamento_a = :agrup ")
    Long checkCupom(@Param("agrup") String agrupamento);

    @Modifying
    @Transactional
    @Query(value = "delete from ticket  where file_id = :fileId", nativeQuery = true)
    void deleteByFileId(@Param("fileId") Long fileId);

    @Modifying
    @Query(value = "delete from ticket  where created_at between :start and :end", nativeQuery = true)
    void deleteByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    //@Query(nativeQuery = true,countQuery = "select count(*) from ticket where status= :status" ,value = "select * from ticket where status= :status")
    List<Ticket> findByStatus(TicketStatusEnum status, Pageable page);

    List<Ticket> findBydataEmissaoBetween(Date startTime, Date endTime);

    List<Ticket> findByAgrupamentoC(String agrupamento);

    List<Ticket> findByAgrupamentoA(String agrupamento);

    List<Ticket> findByFileIdAndStatus(Long id, TicketStatusEnum status, Pageable page);

    List<Ticket> findByFileIdAndStatus(Long id, TicketStatusEnum status);

}
