/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Agency;
import com.core.behavior.model.Calendar;
import com.core.behavior.model.ScoreCard;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface ScoreCardRepository extends JpaRepository<ScoreCard,Long>,JpaSpecificationExecutor<ScoreCard> {
    
    List<ScoreCard> findByAgencyAndCalendar(Agency agency, Calendar calendar); 
    
}
