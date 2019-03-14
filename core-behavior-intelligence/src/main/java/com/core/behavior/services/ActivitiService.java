package com.core.behavior.services;

import com.core.behavior.properties.ActivitiProxyProperties;
import com.core.behavior.request.UserRequest;
import com.core.behavior.response.ListUserResponse;
import com.core.behavior.response.UserResponse;
import com.core.behavior.util.ActivitiConstantePath;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class ActivitiService {

    private RestTemplate proxy;
    
    @Autowired
    private ActivitiProxyProperties properties;
    
    private final String user = "kermit";
    private final String password = "kermit";

    public ActivitiService() {
        proxy = new RestTemplate();
        proxy.getInterceptors().add(new BasicAuthenticationInterceptor(user, password));        
    }

    public UserResponse saveUser(UserRequest user) throws Exception {

        HttpEntity<UserRequest> request = new HttpEntity<>(user);

        String url = properties.getUrl() + ActivitiConstantePath.PATH_POST_USER;

        ResponseEntity<UserResponse> response = proxy.exchange(url, HttpMethod.POST, request, UserResponse.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception("Error Middleware ACTIVITI");
        }

        return response.getBody();
    }

    public List<UserResponse> listUsers() throws Exception {
        String url = properties.getUrl() + ActivitiConstantePath.PATH_GET_USER;
        ResponseEntity<ListUserResponse> response = proxy.getForEntity(url, ListUserResponse.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception("Error Middleware ACTIVITI");
        }

        return response.getBody().getData();
    }
    
    public UserResponse getUser(String userId) throws Exception{
        String url =  properties.getUrl() + ActivitiConstantePath.PATH_GET_USER + "/" + userId;
        ResponseEntity<UserResponse>response = proxy.getForEntity(url, UserResponse.class);
        
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new Exception("Error Middleware ACTIVITI");
        }
        
        return response.getBody();
    }
    
    public void deleteUser(String idUser){
        String url = properties.getUrl() + ActivitiConstantePath.PATH_DELETE_USER.replace("{id}", idUser);        
        proxy.delete(url);
    }
    
    public void updateUser(UserRequest user){
        String url = properties.getUrl() + ActivitiConstantePath.PATH_PUT_USER.replace("{id}", user.getId());        
        HttpEntity<UserRequest>request = new HttpEntity<>(user);
        proxy.exchange(url, HttpMethod.PUT, request, Void.class);
    }

}
