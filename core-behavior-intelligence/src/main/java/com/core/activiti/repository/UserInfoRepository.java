/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.activiti.repository;

import com.core.activiti.model.UserInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

    List<UserInfo> findByKeyAndValue(String key, String value);

    List<UserInfo> findByUserId(String userId);

    @Modifying
    void deleteByUserId(String id);
    
    void deleteByKeyAndValue(String key,String value);
}
