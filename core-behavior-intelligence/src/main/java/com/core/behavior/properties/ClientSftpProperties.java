/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author thiag
 */
@ConfigurationProperties(prefix = "sftp", ignoreUnknownFields = true)
public class ClientSftpProperties {
    
    
    @Getter
    @Setter
    private String user;
    
    @Getter
    @Setter
    private String password;
    
    @Getter
    @Setter
    private String host;
    
    @Getter
    @Setter
    private int port;
    
    @Getter
    @Setter
    private String protocol;
    
    @Getter
    @Setter
    private String basePath;
    
}
