/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.activiti.specifications;

import com.core.activiti.model.GroupMemberActiviti;
import com.core.activiti.model.GroupMemberActiviti_;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class GroupMemberSpecification {
    
    
     public static Specification<GroupMemberActiviti> ids(List<String> list) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get(GroupMemberActiviti_.userId));
            
            for (String u : list) {
                inClause.value(u);
            }            
            return inClause;
        };
    } 
}
