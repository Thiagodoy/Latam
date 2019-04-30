/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.activiti.repository;

import com.core.activiti.model.UserInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {    
    Optional<UserInfo> findByKeyAndValue(String key,String value);
    Optional<UserInfo> findByUserIdAndKey(String userId,String key);
    List<UserInfo> findByUserId(String userId);
}
