/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Calendar;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface CalendarRepository extends JpaRepository<Calendar,Long > {

    List<Calendar> findByDateInit(LocalDate data);
    
    List<Calendar> findByDateEnd(LocalDate data);

    @Query(value = "select c from Calendar c where :data between c.dateInit and c.dateEnd")
    List<Calendar> hasCalendar(@Param("data") LocalDate data);
    
    List<Calendar> findByDateInitBetween(LocalDate init, LocalDate start);
   
    @Query(value = "select * from calendar c where upper(c.period) like :period%", nativeQuery = true)
    Page<Calendar> findByPeriod(@Param("period")String period, Pageable page);

}
