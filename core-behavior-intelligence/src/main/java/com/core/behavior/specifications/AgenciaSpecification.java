package com.core.behavior.specifications;


import com.core.behavior.model.Agency;

import com.core.behavior.model.Agency_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class AgenciaSpecification {
    
    
    public static Specification<Agency> name(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(Agency_.name), name);
    }
    
    public static Specification<Agency> code(String code) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(Agency_.agencyCode), code);
    }  

}
