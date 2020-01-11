/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.specifications;


import com.core.behavior.model.Agency;
import com.core.behavior.model.Calendar;
import com.core.behavior.model.ScoreCard;
import com.core.behavior.model.ScoreCard_;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class ScoreCardSpecification {

    public static Specification<ScoreCard> approved(String approved) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.upper(root.get(ScoreCard_.approved)), approved.toUpperCase());
    }

    public static Specification<ScoreCard> reviewed(String reviewed) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.upper(root.get(ScoreCard_.reviewed)), reviewed.toUpperCase());
    }
    
    public static Specification<ScoreCard> calendar(Calendar calendar) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ScoreCard_.calendar), calendar);
    }
    
    
    public static Specification<ScoreCard> agencys(List<Agency>agencys) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            CriteriaBuilder.In<Agency> inClause = criteriaBuilder.in(root.get(ScoreCard_.agency));
            
            for (Agency u : agencys) {
                inClause.value(u);
            }            
            return inClause;
        };
    }
    

}
