
package com.core.behavior.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@ConfigurationProperties(prefix = "amazonproperties", ignoreUnknownFields = true)
public class AmazonProperties {
    private String endpointUrl;   
    private String bucketName;   
    private String accessKey;   
    private String secretKey;
    
}
