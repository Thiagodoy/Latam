package com.core.activiti.specifications;

import com.core.activiti.model.UserActiviti;
import com.core.activiti.model.UserActiviti_;
import com.core.behavior.dto.UserDTO;
import com.core.behavior.util.UserStatusEnum;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;

import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class UserActivitiSpecification {

    public static Specification<UserActiviti> firstName(String firstName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(UserActiviti_.firstName)), firstName.toUpperCase() + "%");
    }

    public static Specification<UserActiviti> lastName(String lastName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(UserActiviti_.lastName)), lastName.toUpperCase() + "%");
    }

    public static Specification<UserActiviti> email(String email) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(UserActiviti_.email), email + "%");
    }
    
    public static Specification<UserActiviti> userMaster(String id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(UserActiviti_.userMasterId), id);
    }
    
    public static Specification<UserActiviti> status(UserStatusEnum status) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(UserActiviti_.status), status);
    }
    
    public static Specification<UserActiviti> ids(List<UserDTO> list) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get(UserActiviti_.id));
            
            for (UserDTO u : list) {
                inClause.value(u.getId());
            }
            
            return inClause;
        };
    }  

}
