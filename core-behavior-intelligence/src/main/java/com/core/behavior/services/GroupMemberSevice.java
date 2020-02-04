package com.core.behavior.services;

import com.core.activiti.model.GroupMemberActiviti;
import com.core.activiti.repository.GroupMemberAcitvitiRepository;
import com.core.activiti.specifications.GroupMemberSpecification;
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
    
    @Transactional
    public void deleteByUserId(GroupMemberActiviti.IdClass userId){
        repository.deleteById(userId);
    }
    
    
    @Transactional
    public void save(GroupMemberActiviti gma){
        this.repository.save(gma);
    }
    
    public List<GroupMemberActiviti>findById(List<String>ids){
        return repository.findAll(GroupMemberSpecification.ids(ids));
    }
    
    
}
