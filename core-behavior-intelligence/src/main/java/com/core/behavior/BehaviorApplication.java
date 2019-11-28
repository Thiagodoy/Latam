package com.core.behavior;

import com.core.behavior.properties.ActivitiProperties;
import com.core.behavior.properties.ActivitiProxyProperties;
import com.core.behavior.properties.AmazonIntegrationProperties;
import com.core.behavior.properties.AmazonProperties;
import com.core.behavior.properties.AnaliticsProperties;
import com.core.behavior.properties.BehaviorProperties;
import com.core.behavior.properties.ClientSftpProperties;
import com.core.behavior.properties.EmailServiceProperties;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableConfigurationProperties({AnaliticsProperties.class,ClientSftpProperties.class, ActivitiProxyProperties.class, BehaviorProperties.class, ActivitiProperties.class, AmazonProperties.class,AmazonIntegrationProperties.class, EmailServiceProperties.class})
@EnableAutoConfiguration
@ComponentScan()
@EnableWebMvc
@EnableCaching
public class BehaviorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BehaviorApplication.class, args);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
}
