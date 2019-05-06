/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.activiti.repository;

import com.core.activiti.model.GroupMemberActiviti;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface GroupMemberAcitvitiRepository extends CrudRepository<GroupMemberActiviti, GroupMemberActiviti.IdClass>{
    
     void deleteByUserId(String userId);
    
}
