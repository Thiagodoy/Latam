package com.core.behavior.services;

import com.core.behavior.exception.ActivitiException;
import com.core.activiti.model.GroupActiviti;
import com.core.activiti.model.UserActiviti;
import com.core.activiti.repository.GroupActivitiRepository;
import com.core.activiti.repository.UserActivitiRepository;
import com.core.behavior.request.LoginRequest;
import com.core.behavior.request.UserRequest;
import com.core.behavior.response.GroupResponse;
import com.core.behavior.response.UserResponse;
import com.core.activiti.specifications.UserActivitiSpecification;
import com.core.behavior.util.MessageCode;
import java.util.ArrayList;
import java.util.Arrays;
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
public class UserActivitiService {

   

    @Autowired
    private UserActivitiRepository userActivitiRepository;

    @Autowired
    private GroupActivitiRepository groupActivitiRepository;

    @Transactional
    public void deleteUser(String idUser) {
        userActivitiRepository.deleteById(idUser);
    }

    @Transactional
    public void updateUser(UserRequest user) {

        userActivitiRepository.deleteById(user.getId());
        UserActiviti userActiviti = new UserActiviti(user);
        userActivitiRepository.save(userActiviti);

    }

    public UserResponse getUser(String id){
        
        Optional<UserActiviti> opt = userActivitiRepository.findById(id);      
        
        if(!opt.isPresent()){
            throw new ActivitiException(MessageCode.USER_NOT_FOUND_ERROR);
        }      
        
        return getResponseUsers(Arrays.asList(opt.get())).get(0);
    }
    public UserResponse login(LoginRequest request) {

        UserActiviti user = userActivitiRepository.findById(request.getEmail()).orElseThrow(() -> new ActivitiException(MessageCode.USER_NOT_FOUND_ERROR));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ActivitiException(MessageCode.USER_PASSWORD_ERROR);
        }

        List<GroupResponse> listGroupsResponse = new ArrayList();
        List<GroupActiviti> listGroups = groupActivitiRepository.findAll();

        user.getGroups().forEach(g -> {
            GroupActiviti group = listGroups.stream().filter(gg -> gg.getId().equals(g.getGroupId())).findFirst().get();
            listGroupsResponse.add(new GroupResponse(group));
        });

        UserResponse response = new UserResponse(user);
        response.setGroups(listGroupsResponse);

        return response;
    }

    public void saveUsers(UserRequest user) {
        UserActiviti userActiviti = new UserActiviti(user);
        userActivitiRepository.save(userActiviti);
    }

    public Page<UserActiviti> listAllUser(String firstName, String lastName, String email, Pageable page) {

        List<Specification<UserActiviti>> predicates = new ArrayList<>();

        if (Optional.ofNullable(firstName).isPresent()) {

            predicates.add(UserActivitiSpecification.firstName(firstName));
        }
        if (Optional.ofNullable(lastName).isPresent()) {

            predicates.add(UserActivitiSpecification.lastName(lastName));
        }
        if (Optional.ofNullable(email).isPresent()) {
            predicates.add(UserActivitiSpecification.email(email));
        }

        Specification<UserActiviti> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);
        
       return  userActivitiRepository.findAll(specification,page);
       
    }

    private  List<UserResponse> getResponseUsers(List<UserActiviti> list) {

        List<UserResponse> responses = new ArrayList();

        List<GroupActiviti> listGroups = groupActivitiRepository.findAll();

        list.forEach(u -> {

            List<GroupResponse> listGroupsResponse = new ArrayList();

            u.getGroups().forEach(g -> {
                GroupActiviti group = listGroups.stream().filter(gg -> gg.getId().equals(g.getGroupId())).findFirst().get();
                listGroupsResponse.add(new GroupResponse(group));
            });

            UserResponse response = new UserResponse(u);
            response.setGroups(listGroupsResponse);
            responses.add(response);
        });

        return responses;

    }

}
