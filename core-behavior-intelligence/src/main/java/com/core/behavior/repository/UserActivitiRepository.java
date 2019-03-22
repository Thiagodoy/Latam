package com.core.behavior.repository;

import com.core.behavior.model.UserActiviti;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public interface UserActivitiRepository extends CrudRepository<UserActiviti, String> {    
   UserActiviti findByEmail(@Param(value = "email")String email);    
}
