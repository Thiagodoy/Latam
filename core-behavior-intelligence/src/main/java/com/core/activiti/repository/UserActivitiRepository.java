package com.core.activiti.repository;

import com.core.activiti.model.UserActiviti;
import com.core.behavior.dto.UserDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface UserActivitiRepository extends JpaRepository<UserActiviti, String> , JpaSpecificationExecutor<UserActiviti> {    
      
    List<UserDTO>getUserByAgencyAndProfile(@Param(value = "agencys")List<String>agencys, @Param(value = "profile")List<String>profile );
}
