package com.core.behavior.services;

import com.core.behavior.properties.ActivitiProxyProperties;
import com.core.activiti.repository.UserActivitiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class ActivitiService {

     protected RestTemplate proxy;

    @Autowired
    protected ActivitiProxyProperties properties;

    @Autowired
    protected UserActivitiRepository repository;

    private final String user = "kermit";
    private final String password = "kermit";

    public ActivitiService() {
        proxy = new RestTemplate();
        proxy.getInterceptors().add(new BasicAuthenticationInterceptor(user, password));
    }
}
