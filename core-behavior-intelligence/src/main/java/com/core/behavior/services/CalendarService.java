/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;



import com.core.behavior.model.Calendar;
import com.core.behavior.repository.CalendarRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public Optional<Calendar> hasCalendar(LocalDate date) throws Exception {

        List<Calendar> calendars = this.calendarRepository.hasCalendar(date);

        if (calendars.size() > 1) {
            throw new Exception("Existe sobreposição de calendário!");
        }
        return calendars.stream().findFirst();
    }

    public Optional<Calendar> isInitOfCalendar(LocalDate date) throws Exception {

        List<Calendar> calendars = this.calendarRepository.findByDateInit(date);

        if (calendars.size() > 1) {
            throw new Exception("Existe sobreposição de calendário!");
        }
        return calendars.stream().findFirst();

    }

    public Optional<Calendar> isEndOfCalendar(LocalDate date) throws Exception {

        List<Calendar> calendars = this.calendarRepository.findByDateEnd(date);

        if (calendars.size() > 1) {
            throw new Exception("Existe sobreposição de calendário!");
        }
        return calendars.stream().findFirst();

    }

    public Object list(String period, boolean pagination, Pageable page) {

        if (pagination) {

            if (period != null) {
                return this.calendarRepository.findByPeriod(period.toUpperCase(), page);
            } else {
                return this.calendarRepository.findAll(page);
            }

        } else {
            Calendar calendar = calendarRepository.hasCalendar(LocalDate.now()).get(0);

            LocalDate end = calendar.getDateEnd();
            LocalDate init = LocalDate.now().minusMonths(4);
            
            List<Calendar> calendars = calendarRepository.findByDateInitBetween(init, end);
            calendars.sort(Comparator.comparing(Calendar::getDateInit).reversed());
            return calendars.stream().limit(3).collect(Collectors.toList());
        }
    }

    @Transactional
    public void save(Calendar calendar) {
        this.calendarRepository.save(calendar);
    }

    @Transactional
    public void delete(Long id) {
        this.calendarRepository.deleteById(id);
    }

    @Transactional
    public void update(Calendar request) {

        Calendar entity = this.calendarRepository.findById(request.getId()).get();

        if (!request.getDateEnd().equals(entity.getDateEnd())) {
            entity.setDateEnd(request.getDateEnd());
        }

        if (!request.getDateInit().equals(entity.getDateInit())) {
            entity.setDateInit(request.getDateInit());
        }

        if (!request.getPeriod().equals(entity.getPeriod())) {
            entity.setPeriod(request.getPeriod());
        }       

        this.calendarRepository.save(entity);

    }

    public Calendar findById(Long id) {
        return this.calendarRepository.findById(id).get();
    }
}
