package com.core.behavior.configuration;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configurable
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("http://localhost:8002");
        registry.addMapping("https://19c62025.ngrok.io");
        
    }
    
}


