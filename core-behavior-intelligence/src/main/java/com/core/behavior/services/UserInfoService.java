package com.core.behavior.services;

import com.core.activiti.model.UserInfo;
import com.core.activiti.repository.UserInfoRepository;
import com.core.behavior.exception.ActivitiException;
import com.core.behavior.util.Constantes;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class UserInfoService {

    @Autowired
    private UserInfoRepository infoRepository;

    public boolean checkCpfCnpj(String value) {
        Optional<UserInfo> info = infoRepository.findByKeyAndValue("cpfCnpj", value);
        return !info.isPresent();
    }

    public List<UserInfo> findByUser(String id) {
        return infoRepository.findByUserId(id);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void save(UserInfo userInfo) {
        infoRepository.save(userInfo);
    }

    @Transactional
    public void delete(String id) {
        infoRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll(List<UserInfo>list){
        infoRepository.deleteAll(list);
    } 
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void deleteByUserId(String id){
        infoRepository.deleteByUserId(id);
    }
    
    @Transactional
    public void expiredPassword(String id) throws ActivitiException {
        UserInfo passwordExpiration = new UserInfo(id, Constantes.EXPIRATION_PASSWORD, "true");
        infoRepository.save(passwordExpiration);
    }

}
