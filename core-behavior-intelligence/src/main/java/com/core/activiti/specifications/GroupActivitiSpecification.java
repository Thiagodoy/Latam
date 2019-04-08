package com.core.activiti.specifications;

import com.core.activiti.model.GroupActiviti;
import com.core.activiti.model.GroupActiviti_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class GroupActivitiSpecification {

    
    
    
    public static Specification<GroupActiviti> name(String name){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(GroupActiviti_.name), name);
    }
    
    public static Specification<GroupActiviti> type(String type){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(GroupActiviti_.type), type);
    }
    
    public static Specification<GroupActiviti> id(String id){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(GroupActiviti_.id), id);
    }
    
    
}
