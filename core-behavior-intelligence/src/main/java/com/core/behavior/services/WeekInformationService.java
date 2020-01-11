/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;


import com.core.behavior.model.Agency;
import com.core.behavior.model.Calendar;
import com.core.behavior.model.WeekInformation;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.CalendarRepository;
import com.core.behavior.repository.WeekInformationRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class WeekInformationService {

    @Autowired
    private WeekInformationRepository repository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Transactional
    public void save(WeekInformation upload) {
        this.repository.save(upload);
    }

    @Transactional
    public void saveAll(List<WeekInformation> lis) {
        this.repository.saveAll(lis);
    }

    public List<WeekInformation> findByWeekOfYear(Long week, Long year) {
        return this.repository.findByWeekOfYearAndYear(week, year);
    }
    
    public WeekInformation findByWeekOfYearAndYearAndAgency(Long week, Long year, Agency agency) {
        return this.repository.findByWeekOfYearAndYearAndAgency(week, year,agency);
    }
    
    

    public List<WeekInformation> findByAgencyAndCalendar(Agency agency, Calendar calendar) {
        return this.repository.findByAgencyAndCalendar(agency, calendar);
    }

    public List<WeekInformation> find(Long calendar, Long agency) {

        Agency agency1 = this.agencyRepository.findById(agency).get();
        Calendar calendar1 = this.calendarRepository.findById(calendar).get();

        return this.repository.findByAgencyAndCalendar(agency1, calendar1);

    }

}
