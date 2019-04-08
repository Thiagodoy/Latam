package com.core.activiti.specifications;

import com.core.activiti.model.UserActiviti;
import com.core.activiti.model.UserActiviti_;

import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class UserActivitiSpecification {

    public static Specification<UserActiviti> firstName(String firstName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(UserActiviti_.firstName), firstName);
    }

    public static Specification<UserActiviti> lastName(String lastName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(UserActiviti_.lastName), lastName);
    }

    public static Specification<UserActiviti> email(String email) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(UserActiviti_.email), email);
    }

}
