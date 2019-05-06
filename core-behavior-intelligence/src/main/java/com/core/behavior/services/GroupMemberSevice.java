package com.core.behavior.services;

import com.core.activiti.model.GroupMemberActiviti;
import com.core.activiti.repository.GroupMemberAcitvitiRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class GroupMemberSevice {

    @Autowired
    private GroupMemberAcitvitiRepository repository;
    
    @Transactional
    public void deleteAll(List<GroupMemberActiviti>list){
        repository.deleteAll(list);
    }
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void deleteByUserId(String userId){
        repository.deleteByUserId(userId);
    }
    
    
}