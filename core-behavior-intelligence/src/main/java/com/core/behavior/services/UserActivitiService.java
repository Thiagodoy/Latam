package com.core.behavior.services;

import com.core.behavior.exception.ApplicationException;
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
import com.core.behavior.dto.UserDTO;
import com.core.behavior.model.Notificacao;
import com.core.behavior.request.ChangePasswordRequest;
import com.core.behavior.util.Constantes;
import com.core.behavior.util.LayoutEmailEnum;
import com.core.behavior.util.MessageCode;
import com.core.behavior.util.UserStatusEnum;
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
import java.util.concurrent.CopyOnWriteArrayList;
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
    private UserInfoService infoService;

    @Autowired
    private GroupMemberSevice groupMemberSevice;

    @Autowired
    private NotificacaoService notificacaoService;

    @Transactional
    public void deleteUser(String idUser) {
        UserActiviti userActiviti = userActivitiRepository.findById(idUser).get();        
        infoService.deleteAll(userActiviti.getInfo());        
        userActivitiRepository.deleteById(idUser);        
    }

    @Transactional
    public void updateUser(UserRequest user) {

        groupMemberSevice.deleteByUserId(user.getId());
        infoService.deleteByUserId(user.getId());

        UserActiviti userActiviti = userActivitiRepository.findById(user.getId()).orElseThrow(() -> new ApplicationException(MessageCode.USER_NOT_FOUND_ERROR));

        userActiviti.merge(user);
        userActivitiRepository.save(userActiviti);
    }

    public UserResponse getUser(String id) {

        Optional<UserActiviti> opt = userActivitiRepository.findById(id);

        if (!opt.isPresent()) {
            throw new ApplicationException(MessageCode.USER_NOT_FOUND_ERROR);
        }

        return getResponseUsers(Arrays.asList(opt.get())).get(0);
    }

    public List<UserResponse> getUsers(List<String> ids) {

        List<UserResponse> list = new ArrayList();
        CopyOnWriteArrayList c = new CopyOnWriteArrayList(list);
        ids.parallelStream().forEach(i -> {
            Optional<UserActiviti> opt = userActivitiRepository.findById(i);
            UserResponse user = getResponseUsers(Arrays.asList(opt.get())).get(0);
            c.add(user);
        });

        return c;

    }

    @Transactional
    public UserResponse login(LoginRequest request) {

        UserActiviti user = userActivitiRepository
                .findById(request.getEmail())
                .orElseThrow(() -> new ApplicationException(MessageCode.USER_NOT_FOUND_ERROR));

        if (user.getStatus().equals(UserStatusEnum.DISABLED)) {
            throw new ApplicationException(MessageCode.USER_DESATIVADO);
        }

        Optional<UserInfo> expiredAccess = Utils.valueFromUserInfo(user, Constantes.EXPIRATION_ACCESS);
        if (expiredAccess.isPresent() && expiredAccess.get().getValue().equals("true")) {
            throw new ApplicationException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }

        Optional<UserInfo> expiredPassword = Utils.valueFromUserInfo(user, Constantes.EXPIRATION_PASSWORD);
        if (expiredPassword.isPresent() && expiredPassword.get().getValue().equals("true")) {
            throw new ApplicationException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }

        Optional<UserInfo> firstAcess = Utils.valueFromUserInfo(user, Constantes.FIRST_ACCESS);

        if (!Utils.isMaster(user) && firstAcess.isPresent() && firstAcess.get().getValue().equals("true")) {
            LocalDateTime time = LocalDateTime.now().minus(4, ChronoUnit.HOURS);

            Optional<UserInfo> lastAccess = Utils.valueFromUserInfo(user, Constantes.LAST_ACCESS);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateLastAcess = LocalDateTime.parse(lastAccess.get().getValue(), formatter);

            if (time.isAfter(dateLastAcess)) {
                UserInfo passwordExpiration = new UserInfo(user.getId(), Constantes.EXPIRATION_PASSWORD, "true");
                infoService.save(passwordExpiration);
                throw new ApplicationException(MessageCode.EXPIRED_PASSWORD_ERROR);
            }
        }

        Optional<UserInfo> lastAccess = Utils.valueFromUserInfo(user, Constantes.LAST_ACCESS);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate dateAcess = LocalDate.parse(lastAccess.get().getValue(), formatter);
        LocalDate ld = LocalDate.now().minus(180, ChronoUnit.DAYS);

        if (!Utils.isMaster(user) && ld.isAfter(dateAcess)) {
            UserInfo passwordExpiration = new UserInfo(user.getId(), Constantes.EXPIRATION_ACCESS, "true");
            infoService.save(passwordExpiration);
            throw new ApplicationException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ApplicationException(MessageCode.USER_PASSWORD_ERROR);
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

        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        userLastAccess.setValue(formatterDate.format(LocalDateTime.now()));

        infoService.save(userLastAccess);

        return response;
    }

    @Transactional
    public void saveUsers(UserRequest user) throws MessagingException, IOException {

        String password = Utils.generatePasswordRandom();
        user.setPassword(DigestUtils.md5Hex(password));
        UserActiviti userActiviti = new UserActiviti(user);
        userActiviti.setStatus(UserStatusEnum.ACTIVE);
        userActivitiRepository.save(userActiviti);

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":name", Utils.replaceAccentToEntityHtml(user.getFirstName()));
        parameter.put(":email", user.getEmail());
        parameter.put(":password", password);

        Notificacao notificacao = new Notificacao();
        notificacao.setLayout(LayoutEmailEnum.CONGRATS);
        notificacao.setParameters(Utils.mapToString(parameter));
        notificacaoService.save(notificacao);
    }

    @Transactional
    public void resendAccess(String id, boolean master) throws MessagingException, IOException {

        UserActiviti userActiviti = userActivitiRepository.findById(id).orElseThrow(() -> new ApplicationException(MessageCode.USER_NOT_FOUND_ERROR));

        Optional<UserInfo> opt = userActiviti.getInfo().stream().filter(u -> u.getKey().equals(Constantes.EXPIRATION_ACCESS)).findFirst();
        Optional<UserInfo> optPass = userActiviti.getInfo().stream().filter(u -> u.getKey().equals(Constantes.EXPIRATION_PASSWORD)).findFirst();

        if (!master && opt.isPresent()) {
            throw new ApplicationException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }

        if (!master && optPass.isPresent()) {
            throw new ApplicationException(MessageCode.EXPIRED_PASSWORD_ERROR);
        }

        if (master) {
            if (opt.isPresent()) {
                userActiviti.getInfo().remove(opt.get());
                infoService.delete(opt.get().getId());
            }

            if (optPass.isPresent()) {
                userActiviti.getInfo().remove(optPass.get());
                infoService.delete(optPass.get().getId());
            }
        }

        UserInfo primeiroAcesso = Utils.valueFromUserInfo(userActiviti, Constantes.FIRST_ACCESS).get();
        primeiroAcesso.setValue("true");

        UserInfo lastAccess = Utils.valueFromUserInfo(userActiviti, Constantes.LAST_ACCESS).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        lastAccess.setValue(formatter.format(LocalDateTime.now()));

        String password = Utils.generatePasswordRandom();
        userActiviti.setPassword(DigestUtils.md5Hex(password));

        userActivitiRepository.save(userActiviti);
        DateTimeFormatter data = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":name", Utils.replaceAccentToEntityHtml(userActiviti.getFirstName()));
        parameter.put(":email", userActiviti.getEmail());
        parameter.put(":password", password);
        parameter.put(":data", data.format(LocalDateTime.now()));

        Notificacao notificacao = new Notificacao();
        notificacao.setLayout(LayoutEmailEnum.FORGOT);
        notificacao.setParameters(Utils.mapToString(parameter));
        notificacaoService.save(notificacao);

    }

    @Transactional
    public void forgotAccess(String id) throws MessagingException, IOException {

        Optional<UserActiviti> opt = userActivitiRepository.findById(id);

        if (!opt.isPresent()) {
            throw new ApplicationException(MessageCode.USER_NOT_FOUND_ERROR);
        }

        Optional<UserInfo> expiredAccess = Utils.valueFromUserInfo(opt.get(), Constantes.EXPIRATION_ACCESS);
        if (expiredAccess.isPresent() && expiredAccess.get().getValue().equals("true")) {
            throw new ApplicationException(MessageCode.EXPIRED_LOGIN_45_DAYS_ERROR);
        }

        Optional<UserInfo> expiredPassword = Utils.valueFromUserInfo(opt.get(), Constantes.EXPIRATION_PASSWORD);
        if (expiredPassword.isPresent() && expiredPassword.get().getValue().equals("true")) {
            throw new ApplicationException(MessageCode.EXPIRED_PASSWORD_ERROR);
        }

        UserActiviti userActiviti = opt.get();
        String password = Utils.generatePasswordRandom();
        userActiviti.setPassword(DigestUtils.md5Hex(password));

        Optional<UserInfo> op = Utils.valueFromUserInfo(opt.get(), Constantes.FIRST_ACCESS);
        if (op.isPresent()) {
            op.get().setValue("true");
        }

        userActivitiRepository.save(userActiviti);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(":name", Utils.replaceAccentToEntityHtml(userActiviti.getFirstName()));
        parameter.put(":email", userActiviti.getEmail());
        parameter.put(":password", password);
        parameter.put(":data", formatter.format(LocalDateTime.now()));

        Notificacao notificacao = new Notificacao();
        notificacao.setLayout(LayoutEmailEnum.FORGOT);
        notificacao.setParameters(Utils.mapToString(parameter));
        notificacaoService.save(notificacao);

    }

    public Page<UserActiviti> listAllUser(String firstName, String lastName, String email, String[] profile, String[] agencys, Pageable page) {

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

        if (profile != null && agencys != null) {
            List<UserDTO> list = userActivitiRepository.getUserByAgencyAndProfile(Arrays.asList(agencys), Arrays.asList(profile));

            if (!list.isEmpty()) {
                predicates.add(UserActivitiSpecification.ids(list));
            } else {
                //quando não houver nenhum usuário força retorno de uma lista vazia
                predicates.add(UserActivitiSpecification.ids(Arrays.asList(new UserDTO("-1"))));
            }
        }

        predicates.add(UserActivitiSpecification.status(UserStatusEnum.ACTIVE));

        Specification<UserActiviti> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return userActivitiRepository.findAll(specification, page);

    }

    private List<UserResponse> getResponseUsers(List<UserActiviti> list) {

        List<UserResponse> responses = new ArrayList();

        List<GroupActiviti> listGroups = groupActivitiRepository.findAll();

        list.forEach(u -> {

            List<GroupResponse> listGroupsResponse = new ArrayList();
            u.getGroups();
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
            throw new ApplicationException(MessageCode.USER_NOT_FOUND_ERROR);
        }

        if (!opt.get().getPassword().equals(request.getPassword())) {
            throw new ApplicationException(MessageCode.USER_PASSWORD_ERROR);
        }

        UserActiviti user = opt.get();

        user.setPassword(request.getNewPassword());

        if (request.isFirstAccess()) {

            Optional<UserInfo> op = user.getInfo().stream().filter(i -> i.getKey().equals("primeiro_acesso")).findFirst();

            if (op.isPresent()) {
                op.get().setValue("false");
            }
        }

        // Altera o contador  da mudanca de senha
        Optional<UserInfo> op = user.getInfo().stream().filter(i -> i.getKey().equals(Constantes.CHANGE_PASSWORD)).findFirst();
        if (op.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            op.get().setValue(formatter.format(LocalDateTime.now()));
        }

        userActivitiRepository.save(user);

    }

}
