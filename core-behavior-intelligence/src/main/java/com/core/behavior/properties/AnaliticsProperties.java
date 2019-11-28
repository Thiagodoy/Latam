/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author thiag
 */
@ConfigurationProperties(prefix = "analitics", ignoreUnknownFields = true)
@Data
public class AnaliticsProperties {

    private String url;
    private String user;
    private String password;    
    
}
