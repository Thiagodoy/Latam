package com.core.behavior;

import com.core.behavior.properties.ActivitiProperties;
import com.core.behavior.properties.ActivitiProxyProperties;
import com.core.behavior.properties.AmazonProperties;
import com.core.behavior.properties.BehaviorProperties;
import com.core.behavior.properties.EmailServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableConfigurationProperties({ActivitiProxyProperties.class, BehaviorProperties.class, ActivitiProperties.class, AmazonProperties.class, EmailServiceProperties.class})
@EnableAutoConfiguration
@ComponentScan()
@EnableWebMvc
public class BehaviorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BehaviorApplication.class, args);
    }
    
    

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurerAdapter() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/user/login").allowedOrigins("http://localhost:8080");
//            }
//        };
//    }
    
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedOrigin("http://localhost:8080");
//        corsConfiguration.setAllowedMethods(Arrays.asList(
//                HttpMethod.GET.name(),
//                HttpMethod.HEAD.name(),
//                HttpMethod.POST.name(),
//                HttpMethod.PUT.name(),
//                HttpMethod.OPTIONS.name(),
//                HttpMethod.DELETE.name()));
//        corsConfiguration.setMaxAge(1800L);
//        source.registerCorsConfiguration("/user/login", corsConfiguration); // you restrict your path here
//        return source;
//    }
}
