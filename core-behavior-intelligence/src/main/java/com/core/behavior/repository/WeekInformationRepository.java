/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Agency;
import com.core.behavior.model.Calendar;
import com.core.behavior.model.WeekInformation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface WeekInformationRepository extends JpaRepository<WeekInformation, Long>{
    
    List<WeekInformation> findByWeekOfYearAndYear(Long week, Long year);
    
    WeekInformation findByWeekOfYearAndYearAndAgency(Long week, Long year,Agency agency);
    
    List<WeekInformation>findByAgencyAndCalendar(Agency agency, Calendar calendar);
    
}
