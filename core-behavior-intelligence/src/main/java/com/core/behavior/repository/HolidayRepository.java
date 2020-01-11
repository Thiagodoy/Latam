/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Holiday;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long>, JpaSpecificationExecutor<Holiday>{    
    
    List<Holiday> findByDate(LocalDate data);    
        
    List<Holiday> findByDateBetween(LocalDate start,LocalDate end);
    
    Page<Holiday> findByMonthAndYear(Long month,Long year, Pageable page);
    Page<Holiday> findByMonth(Long month , Pageable page);
    Page<Holiday> findByYear(Long year , Pageable page);
    
}
