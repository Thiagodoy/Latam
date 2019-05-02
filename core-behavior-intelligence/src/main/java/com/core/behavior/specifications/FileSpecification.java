package com.core.behavior.specifications;


import com.core.behavior.model.File;

import com.core.behavior.model.File_;
import com.core.behavior.util.StatusEnum;
       

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder.In;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class FileSpecification {
    
    
    public static Specification<File> fileName(String firstName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(File_.name), firstName);
    }
    
    public static Specification<File> userId(String userId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(File_.name), userId);
    }

    public static Specification<File> dateCreated(LocalDateTime dateCreated) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(File_.createdDate), dateCreated);
    }

    public static Specification<File> company(List<Long> company) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            In<Long> inClause = criteriaBuilder.in(root.get(File_.company));
            
            for (Long long1 : company) {
                inClause.value(long1);
            }
            
            return inClause;
        };
    }  
    
    public static Specification<File> status(StatusEnum status) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(File_.status), status);
    }  
    
    public static Specification<File> disLikestatus(StatusEnum status) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(File_.status), status);
    }
    
    public static Specification<File> dateBetweem(LocalDateTime start, LocalDateTime end) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.between(root.get(File_.createdDate), start, end );
    }
    
       

}
