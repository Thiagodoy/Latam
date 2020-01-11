/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Agency;
import com.core.behavior.model.Calendar;
import com.core.behavior.model.NoteIm;

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
public interface NoteInRepository extends JpaRepository<NoteIm,Long>,JpaSpecificationExecutor<NoteIm> {
    
    Page<NoteIm>findByAgencyAndCalendar(Agency agency,Calendar calendar, Pageable page);
    List<NoteIm>findByAgencyAndCalendar(Agency agency,Calendar calendar);
    Page<NoteIm>findByAgency(Long agency, Pageable page);
    
}
