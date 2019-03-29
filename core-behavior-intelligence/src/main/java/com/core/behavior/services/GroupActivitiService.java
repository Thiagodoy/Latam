package com.core.behavior.services;

import com.core.behavior.activiti.response.PageResponse;
import com.core.behavior.model.GroupActiviti;
import com.core.behavior.repository.GroupActivitiRepository;
import com.core.behavior.request.GroupRequest;
import com.core.behavior.response.GroupResponse;
import com.core.behavior.specifications.GroupActivitiSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class GroupActivitiService extends ActivitiService {

    @Autowired
    private GroupActivitiRepository repository;

    @Transactional
    public void saveGroup(GroupRequest request) {
        GroupActiviti group = new GroupActiviti(request);
        repository.save(group);
    }

    @Transactional
    public void deleteGroup(String id) {
        repository.deleteById(id);
    }

    @Transactional
    public void updateGroup(GroupRequest request) {
        repository.deleteById(request.getId());
        repository.save(new GroupActiviti(request));
    }

    public Page<GroupActiviti> getGroup(String id, String name, String type, Pageable page) {

        List<Specification<GroupActiviti>> specifications = new ArrayList<>();
        if (Optional.ofNullable(id).isPresent()) {
            specifications.add(GroupActivitiSpecification.id(id));
        }
        if (Optional.ofNullable(name).isPresent()) {
            specifications.add(GroupActivitiSpecification.id(name));
        }
        if (Optional.ofNullable(type).isPresent()) {
            specifications.add(GroupActivitiSpecification.id(type));
        }
        
        Specification<GroupActiviti> specification = specifications.stream().reduce((a,b)->a.and(b)).orElse(null);
        
        return  repository.findAll(specification, page);
        
       
        
        
        

    }

}
