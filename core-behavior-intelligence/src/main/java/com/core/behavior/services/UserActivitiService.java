package com.core.behavior.services;

import com.core.behavior.exception.ActivitiException;
import com.core.activiti.model.GroupActiviti;
import com.core.activiti.model.UserActiviti;
import com.core.activiti.model.UserInfo;
import com.core.activiti.repository.GroupActivitiRepository;
import com.core.activiti.repository.UserActivitiRepository;
import com.core.behavior.request.LoginRequest;
import com.core.behavior.request.UserRequest;
import com.core.behavior.response.GroupResponse;
import com.core.behavior.response.UserResponse;
import com.core.activiti.specifications.UserActivitiSpecification;
import com.core.behavior.request.ChangePasswordRequest;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.EmailLayoutEnum;
import com.core.behavior.util.MessageCode;
import com.core.behavior.util.Utils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserInfoService infoService;
    
    @Autowired
    private GroupMemberSevice groupMemberSevice;

    @Transactional
    public void deleteUser(String idUser) {
        userActivitiRepository.deleteById(idUser);
    }

    @Transactional
    public void updateUser(UserRequest user) {
        
        
        groupMemberSevice.deleteByUserId(user.getId());
        infoService.deleteByUserId(user.getId());
        
        UserActiviti userActiviti =  userActivitiRepository.findById(user.getId()).orElseThrow(()-> new ActivitiException(MessageCode.USER_NOT_FOUND_ERROR)); 
        
        userActiviti.merge(user);
        userActivitiRepository.save(userActiviti);
    }

    public UserResponse getUser(String id) {

        Optional<UserActiviti> opt = userActivitiRepository.findById(id);

        if (!opt.isPresent()) {
            throw new ActivitiException(MessageCode.USER_NOT_FOUND_ERROR);
        }

        return getResponseUsers(Arrays.asList(opt.get())).get(0);
    }

    @Transactional
    public UserResponse login(LoginRequest request) {

        UserActiviti user = userActivitiRepository
                .findById(request.getEmail())
                .orElseThrow(() -> new ActivitiException(MessageCode.USER_NOT_FOUND_ERROR));       

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ActivitiException(MessageCode.USER_PASSWORD_ERROR);
        }

        Optional<UserInfo> expiredAccess = Utils.valueFromUserInfo(user, Constantes.EXPIRATION_ACCESS);
        if (expiredAccess.isPresent() && expiredAccess.get().getValue().equals("true")) {
            throw new ActivitiException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }
        
        Optional<UserInfo> expiredPassword = Utils.valueFromUserInfo(user, Constantes.EXPIRATION_PASSWORD);
        if (expiredPassword.isPresent() && expiredPassword.get().getValue().equals("true")) {
            throw new ActivitiException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }

        Optional<UserInfo> firstAcess = Utils.valueFromUserInfo(user, Constantes.FIRST_ACCESS);

        if (firstAcess.isPresent() && firstAcess.get().getValue().equals("true")) {
            LocalDateTime time = LocalDateTime.now().minus(24, ChronoUnit.HOURS);

            if (time.isAfter(user.getCreatedAt())) {
                UserInfo passwordExpiration = new UserInfo(user.getId(), Constantes.EXPIRATION_PASSWORD, "true");
                infoService.save(passwordExpiration);
                throw new ActivitiException(MessageCode.EXPIRED_PASSWORD_ERROR);
            }
        }

        Optional<UserInfo> lastAccess = Utils.valueFromUserInfo(user, Constantes.LAST_ACCESS);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateAcess = LocalDate.parse(lastAccess.get().getValue(), formatter);
        LocalDate ld = LocalDate.now().minus(45, ChronoUnit.DAYS);

        if (ld.isAfter(dateAcess)) {
            UserInfo passwordExpiration = new UserInfo(user.getId(), Constantes.EXPIRATION_ACCESS, "true");
            infoService.save(passwordExpiration);
            throw new ActivitiException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }

        List<GroupResponse> listGroupsResponse = new ArrayList();
        List<GroupActiviti> listGroups = groupActivitiRepository.findAll();

        user.getGroups().forEach(g -> {
            GroupActiviti group = listGroups.stream().filter(gg -> gg.getId().equals(g.getGroupId())).findFirst().get();
            listGroupsResponse.add(new GroupResponse(group));
        });

        UserResponse response = new UserResponse(user);
        response.setGroups(listGroupsResponse);

        UserInfo userLastAccess = user.getInfo().stream().filter(f -> f.getKey().equals(Constantes.LAST_ACCESS)).findFirst().get();

        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        userLastAccess.setValue(formatterDate.format(LocalDate.now()));
        
        infoService.save(userLastAccess);

        return response;
    }

    @Transactional
    public void saveUsers(UserRequest user) throws MessagingException, IOException {

        String password = Utils.generatePasswordRandom();
        user.setPassword(DigestUtils.md5Hex(password));
        UserActiviti userActiviti = new UserActiviti(user);
        userActivitiRepository.save(userActiviti);

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":name", user.getFirstName());
        parameter.put(":email", user.getEmail());
        parameter.put(":password", password);

        emailService.send(EmailLayoutEnum.CONGRATS, "Acesso", parameter, user.getEmail());

    }

    @Transactional
    public void resendAccess(String id, boolean master) throws MessagingException, IOException {

        UserActiviti userActiviti = userActivitiRepository.findById(id).orElseThrow(() -> new ActivitiException(MessageCode.USER_NOT_FOUND_ERROR));

        Optional<UserInfo> opt = userActiviti.getInfo().stream().filter(u -> u.getKey().equals(Constantes.EXPIRATION_ACCESS)).findFirst();
        Optional<UserInfo> optPass = userActiviti.getInfo().stream().filter(u -> u.getKey().equals(Constantes.EXPIRATION_PASSWORD)).findFirst();

        if (!master && opt.isPresent()) {
            throw new ActivitiException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }
        
        if (!master && optPass.isPresent()) {
            throw new ActivitiException(MessageCode.EXPIRED_PASSWORD_ERROR);
        }

        if (master) {
            if (opt.isPresent()) {
                infoService.delete(opt.get().getId());
                infoService.delete(optPass.get().getId());
            }
        }

        String password = Utils.generatePasswordRandom();
        userActiviti.setPassword(DigestUtils.md5Hex(password));

        userActivitiRepository.save(userActiviti);

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":name", userActiviti.getFirstName());
        parameter.put(":email", userActiviti.getEmail());
        parameter.put(":password", password);

        emailService.send(EmailLayoutEnum.CONGRATS, "Acesso", parameter, userActiviti.getEmail());
    }

    @Transactional
    public void forgotAccess(String id) throws MessagingException, IOException {

        Optional<UserActiviti> opt = userActivitiRepository.findById(id);

        if (!opt.isPresent()) {
            throw new ActivitiException(MessageCode.USER_NOT_FOUND_ERROR);
        }

        Optional<UserInfo> expiredAccess = Utils.valueFromUserInfo(opt.get(), Constantes.EXPIRATION_ACCESS);
        if (expiredAccess.isPresent() && expiredAccess.get().getValue().equals("true")) {
            throw new ActivitiException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }
        
        Optional<UserInfo> expiredPassword = Utils.valueFromUserInfo(opt.get(), Constantes.EXPIRATION_PASSWORD);
        if (expiredPassword.isPresent() && expiredPassword.get().getValue().equals("true")) {
            throw new ActivitiException(MessageCode.EXPIRED_PASSWORD_ERROR);
        }
        

        UserActiviti userActiviti = opt.get();
        String password = Utils.generatePasswordRandom();
        userActiviti.setPassword(DigestUtils.md5Hex(password));

        Optional<UserInfo> op = Utils.valueFromUserInfo(opt.get(), Constantes.FIRST_ACCESS);
        if (op.isPresent()) {
            op.get().setValue("true");
        }

        userActivitiRepository.save(userActiviti);

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":name", userActiviti.getFirstName());
        parameter.put(":email", userActiviti.getEmail());
        parameter.put(":password", password);

        emailService.send(EmailLayoutEnum.FORGOT, "Acesso", parameter, userActiviti.getEmail());
    }

    public Page<UserActiviti> listAllUser(String firstName, String lastName, String email, String userMaster, Pageable page) {

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
        if(Optional.ofNullable(userMaster).isPresent()){
            predicates.add(UserActivitiSpecification.userMaster(userMaster));
        }

        Specification<UserActiviti> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return userActivitiRepository.findAll(specification, page);

    }

    private List<UserResponse> getResponseUsers(List<UserActiviti> list) {

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

    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        Optional<UserActiviti> opt = userActivitiRepository.findById(request.getEmail());

        if (!opt.isPresent()) {
            throw new ActivitiException(MessageCode.USER_NOT_FOUND_ERROR);
        }

        if (!opt.get().getPassword().equals(request.getPassword())) {
            throw new ActivitiException(MessageCode.USER_PASSWORD_ERROR);
        }

        UserActiviti user = opt.get();

        user.setPassword(request.getNewPassword());

        if (request.isFirstAccess()) {

            Optional<UserInfo> op = user.getInfo().stream().filter(i -> i.getKey().equals("primeiro_acesso")).findFirst();

            if (op.isPresent()) {
                op.get().setValue("false");
            }
        }
        userActivitiRepository.save(user);

    }

}
