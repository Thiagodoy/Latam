/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.specifications;


import com.core.behavior.model.Holiday;
import com.core.behavior.model.Holiday_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class HolidaySpecification {
    
    
    public static Specification<Holiday> byDescription(String description ) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Holiday_.description)), description.toUpperCase() + "%");
    }  
    
    public static Specification<Holiday> byYear(Long year ) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Holiday_.year), year);
    } 
    
    public static Specification<Holiday> byMonth(Long month ) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Holiday_.month), month);
    }     

}
