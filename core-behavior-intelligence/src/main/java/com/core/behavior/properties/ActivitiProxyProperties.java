package com.core.behavior.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@ConfigurationProperties(prefix = "activitiproxy", ignoreUnknownFields = true)
public class ActivitiProxyProperties {
     private String url;   
}
