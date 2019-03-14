package com.core.behavior;

import com.core.behavior.properties.ActivitiProxyProperties;
import com.core.behavior.properties.BehaviorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableConfigurationProperties({ActivitiProxyProperties.class, BehaviorProperties.class})
@EnableAutoConfiguration
public class BehaviorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BehaviorApplication.class, args);
	}

}
