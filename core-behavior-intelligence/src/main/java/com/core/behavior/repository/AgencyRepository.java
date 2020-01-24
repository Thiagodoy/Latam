/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.dto.ConsolidateOrCompanyDTO;
import com.core.behavior.dto.DayStatusDTO;
import com.core.behavior.dto.FrequencyStatusDTO;
import com.core.behavior.dto.ResultRules2;
import com.core.behavior.model.Agency;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> , JpaSpecificationExecutor<Agency>{
    
    @Query(nativeQuery = true)
    DayStatusDTO checkDayStatus(@Param("data") LocalDate date, @Param("codeAgency") String codeAgency, @Param("idCompany") Long idCompany);
    
    @Query(nativeQuery = true)
    ResultRules2 checkRules2(@Param("dateStart")LocalDate start,@Param("dateEnd") LocalDate end,@Param("agencia") Long id);
    
    @Query(nativeQuery = true)
    ConsolidateOrCompanyDTO checkConsolidateOrCompanyStatus(@Param("start")LocalDate start, @Param("end") LocalDate end, @Param("code_agency") String code);
    
    @Query(nativeQuery = true)
    List<FrequencyStatusDTO> checkStatusFrequency(@Param("start")LocalDate start, @Param("end") LocalDate end, @Param("code") String code);
    
}
