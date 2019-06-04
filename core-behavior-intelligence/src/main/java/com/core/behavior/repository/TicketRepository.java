/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.dto.TicketDuplicityDTO;
import com.core.behavior.model.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<TicketDuplicityDTO>listDuplicityByFileId(Long fileId);
    
    @Query(nativeQuery = true, value = "select * from ticket t where t.file_id = :fileId ")
    List<Ticket>findByFileId(@Param("fileId")Long fileId);
    
    
}
