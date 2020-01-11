/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;


import com.core.behavior.model.Agency;
import com.core.behavior.model.Calendar;
import com.core.behavior.model.ScoreCard;
import com.core.behavior.repository.ScoreCardRepository;
import com.core.behavior.request.ScoreCardRequest;
import com.core.behavior.specifications.ScoreCardSpecification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
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
public class ScoreCardService {
    
    @Autowired
    private ScoreCardRepository repository;
    
    
    public void save(ScoreCard scoreCard){
        this.repository.save(scoreCard);
    }
    
    public void saveAll(List<ScoreCard> scoreCards){
        this.repository.saveAll(scoreCards);
    }
    
    public ScoreCard findByAgencyAndCalendar(Agency agency, Calendar calendar){        
        return this.repository.findByAgencyAndCalendar(agency, calendar).stream().findFirst().get();
    } 
    
    
    public Page<ScoreCard> findByAgencyAndCalendar(List<Long>agency, Long calendar, String approved, String reviewed, PageRequest page ){        
        
        
        List<Specification<ScoreCard>>predicates = new ArrayList<>();
        
        if(Optional.ofNullable(calendar).isPresent()){
            Calendar calen = new Calendar();
            calen.setId(calendar);
            predicates.add(ScoreCardSpecification.calendar(calen));            
        }
        
        
        if(Optional.ofNullable(agency).isPresent()){            
            
            List agencys = agency.stream().map(id ->{
                Agency agency1 = new Agency();
                agency1.setId(id);
                return agency1;
            }).collect(Collectors.toList());
            
            predicates.add(ScoreCardSpecification.agencys(agencys));            
        }
        
        if(Optional.ofNullable(approved).isPresent()){            
            predicates.add(ScoreCardSpecification.approved(approved));
        }
        
        if(Optional.ofNullable(reviewed).isPresent()){            
            predicates.add(ScoreCardSpecification.reviewed(reviewed));
        }        
        
        Specification filter = predicates.stream().reduce((a,b)-> a.and(b)).orElseGet(null);
        
        return  this.repository.findAll(filter, page);
    } 
    
    @Transactional
    public void update(ScoreCardRequest request) throws Exception{
        
        Optional<ScoreCard> optionalScoreCard = this.repository.findById(request.getId());
        
        if(!optionalScoreCard.isPresent()){
            throw new Exception("Scorecard não encontrado na base!");
        }
        
        ScoreCard scoreCard = optionalScoreCard.get();
        if(request.getAdjustedResult() != null && !request.getAdjustedResult().equals(scoreCard.getAdjustedResult())){
            scoreCard.setAdjustedResult(request.getAdjustedResult());
            scoreCard.setAdjustedDatetime(LocalDateTime.now());
        }
        
        if(request.getAdjustedUserId() != null && !request.getAdjustedUserId().equals(scoreCard.getAdjustedUserId())){
            scoreCard.setAdjustedUserId(request.getAdjustedUserId());
        }
        
        if(request.getApproved() != null && !request.getApproved().equals(scoreCard.getApproved())){
            scoreCard.setApproved(request.getApproved());
            scoreCard.setApprovedDatetime(LocalDateTime.now());
        }
        
        if(request.getComments() != null && !request.getComments().equals(scoreCard.getComments())){
            scoreCard.setComments(request.getComments());
        }
        
        if(request.getApprovedUserId() != null && !request.getApprovedUserId().equals(request.getApprovedUserId())){
            scoreCard.setApprovedUserId(request.getApprovedUserId());
            scoreCard.setApprovedDatetime(LocalDateTime.now());
        }
        
        if(request.getResult() != null && !request.getResult().equals(request.getResult())){
            scoreCard.setResult(request.getResult());            
        }
        
        if(request.getReviewed() != null && !request.getReviewed().equals(scoreCard.getReviewed())){
            scoreCard.setReviewed(request.getReviewed());
        }
        
        if(request.getCommentsAnalyst()!= null && !request.getCommentsAnalyst().equals(scoreCard.getCommentsAnalyst())){
            scoreCard.setCommentsAnalyst(request.getCommentsAnalyst());
        }
        
        this.repository.save(scoreCard);
    }
    
    
}
