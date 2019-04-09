package com.core.behavior;

import com.core.behavior.properties.ActivitiProperties;
import com.core.behavior.properties.ActivitiProxyProperties;
import com.core.behavior.properties.BehaviorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableConfigurationProperties({ActivitiProxyProperties.class, BehaviorProperties.class, ActivitiProperties.class})
@EnableAutoConfiguration
@ComponentScan()
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
