package com.core.behavior.specifications;

import com.core.behavior.model.UserActiviti;
import com.core.behavior.model.UserActiviti_;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

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
