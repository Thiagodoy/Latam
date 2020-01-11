/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.model.Holiday;
import com.core.behavior.repository.HolidayRepository;
import com.core.behavior.specifications.HolidaySpecification;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class HolidayService {

    @Autowired
    private HolidayRepository repository;

    public boolean isHolidayOrWeekend(LocalDate date) {

        //Não consolida no sábado e no domingo regra para consolidação  ( data - 1 )
        if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY) || date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            return true;
        }

        List<Holiday> holidays = this.repository.findByDate(date);

        return holidays.stream().findFirst().isPresent();
    }

    public List<Holiday> getHolidaysBetween(LocalDate init, LocalDate end) {
        return this.repository.findByDateBetween(init, end);
    }

    public Holiday getByDate(LocalDate date) {
        return this.repository.findByDate(date).stream().findFirst().orElse(new Holiday());
    }

    public Page<Holiday> list(Pageable page, Long month, Long year, String description) {

        List<Specification<Holiday>> predicates = new ArrayList<>();

        if (Optional.ofNullable(description).isPresent()) {
            predicates.add(HolidaySpecification.byDescription(description));
        }
        if (Optional.ofNullable(month).isPresent()) {
            predicates.add(HolidaySpecification.byMonth(month));
        }
        if (Optional.ofNullable(year).isPresent()) {
            predicates.add(HolidaySpecification.byYear(year));
        }        
        
        Specification filter = predicates.size() == 0 ? null :  predicates.stream().reduce((a, b) -> a.and(b)).orElseGet(null);
        
        return this.repository.findAll(filter,page);

    }

    @Transactional
    public void save(Holiday holiday) {
        this.repository.save(holiday);
    }

    @Transactional
    public void update(Holiday request) {

        Holiday entity = this.repository.findById(request.getId()).get();

        if (!request.getDate().equals(entity.getDate())) {
            entity.setDate(request.getDate());
        }

        if (!request.getDescription().equals(entity.getDescription())) {
            entity.setDescription(request.getDescription());
        }

        if (!request.getDay().equals(entity.getDay())) {
            entity.setDay(request.getDay());
        }

        if (!request.getMonth().equals(entity.getMonth())) {
            entity.setMonth(request.getMonth());
        }

        if (!request.getYear().equals(entity.getYear())) {
            entity.setYear(request.getYear());
        }

        this.repository.save(entity);

    }

    @Transactional
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

}
