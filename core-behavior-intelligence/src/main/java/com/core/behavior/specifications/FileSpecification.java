package com.core.behavior.specifications;


import com.core.behavior.model.File;
import com.core.behavior.model.File_;
import java.time.LocalDateTime;
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

    public static Specification<File> company(String company) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(File_.company), company);
    }  
    
    
       

}
