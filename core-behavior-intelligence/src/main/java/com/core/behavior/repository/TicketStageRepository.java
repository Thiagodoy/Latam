/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.TicketStage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface TicketStageRepository extends JpaRepository<TicketStage, Long> {   
    @Query( nativeQuery = true, value = "select * from ticket_stage t where t.agrupamento_a = :agrupa and t.bilhete_behavior != :bilhete and t.cupom = :cupo ")
    Optional<TicketStage> findByAgrupamentoAAndCupom(@Param("agrupa")String agrpamentoA, @Param("cupo")Long Cupom, @Param("bilhete")String bilhete);
    
}
