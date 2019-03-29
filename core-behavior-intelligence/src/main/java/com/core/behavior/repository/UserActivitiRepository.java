package com.core.behavior.repository;

import com.core.behavior.model.UserActiviti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface UserActivitiRepository extends JpaRepository<UserActiviti, String> , JpaSpecificationExecutor<UserActiviti> {    
      
}
