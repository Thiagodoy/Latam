/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.model.Agency;
import com.core.behavior.model.Calendar;
import com.core.behavior.model.NoteIm;
import com.core.behavior.repository.NoteInRepository;
import com.core.behavior.request.NoteImRequest;
import com.core.behavior.specifications.NoteImSpecification;
import com.core.behavior.specifications.ScoreCardSpecification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class NoteImService {

    @Autowired
    private NoteInRepository repository;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private CalendarService calendarService;

    public void save(NoteIm noteIm) {
        this.repository.save(noteIm);
    }

    public void saveAll(List<NoteIm> noteIms) {
        this.repository.saveAll(noteIms);
    }

    public NoteIm findByAgencyAndCalendar(Agency agency, Calendar calendar) {
        return this.repository.findByAgencyAndCalendar(agency, calendar).stream().findFirst().get();
    }

    public Page<NoteIm> findByAgencyAndCalendar(List<Long> agency, Long calendar, PageRequest page) {

        List<Specification<NoteIm>> predicates = new ArrayList<>();

        if (Optional.ofNullable(agency).isPresent()) {

            List agencys = agency.stream().map(id -> {
                Agency agency1 = new Agency();
                agency1.setId(id);
                return agency1;
            }).collect(Collectors.toList());

            predicates.add(NoteImSpecification.agencys(agencys));
        }
        
        if(calendar != null){
            Calendar calendar1 = new Calendar();
            calendar1.setId(calendar);
            predicates.add(NoteImSpecification.calendar(calendar1));
        }

        Specification filter = predicates.stream().reduce((a, b) -> a.and(b)).orElseGet(null);

        return this.repository.findAll(filter, page);
    }

    public Page<NoteIm> findByAgency(List<Long> agency, PageRequest page) {

        List<Specification<NoteIm>> predicates = new ArrayList<>();

        if (Optional.ofNullable(agency).isPresent()) {

            List agencys = agency.stream().map(id -> {
                Agency agency1 = new Agency();
                agency1.setId(id);
                return agency1;
            }).collect(Collectors.toList());

            predicates.add(ScoreCardSpecification.agencys(agencys));
        }

        Specification filter = predicates.stream().reduce((a, b) -> a.and(b)).orElseGet(null);

        return this.repository.findAll(filter, page);
    }

    public void update(NoteImRequest request) throws Exception {

        Optional<NoteIm> noteIm = this.repository.findById(request.getId());

        if (!noteIm.isPresent()) {
            throw new Exception("NoteIm não encontrado!");
        }

        NoteIm noteIm1 = noteIm.get();

        if (request.getDelivered() != null && !request.getDelivered().equals(noteIm1.getDelivered())) {
            noteIm1.setDelivered(request.getDelivered());
            noteIm1.setDateTime(LocalDateTime.now());
        }

        if (request.getUser() != null && !request.getUser().equals(noteIm1.getUser())) {
            noteIm1.setUser(request.getUser());
        }

        this.repository.save(noteIm1);

    }

}
