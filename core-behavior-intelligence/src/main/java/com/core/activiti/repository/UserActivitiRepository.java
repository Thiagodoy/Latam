package com.core.activiti.repository;

import com.core.activiti.model.UserActiviti;
import com.core.behavior.dto.UserDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface UserActivitiRepository extends JpaRepository<UserActiviti, String> , JpaSpecificationExecutor<UserActiviti> {    
      
    List<UserDTO>getUserByAgencyAndProfile(@Param(value = "agencys")List<String>agencys, @Param(value = "profile")List<String>profile );
    
    @Transactional(transactionManager = "activitiTransactionManager")
    @Modifying
    @Query(nativeQuery = true, value = "update act_id_user set pwd_ = :pass where id_ = :id")
    public void updatePassWord(@Param("pass")String pass, @Param("id")String id);
    
}
