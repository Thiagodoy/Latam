package com.core.behavior.configuration;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**");
        //registry.addMapping("http://10.93.1.166:8002");
        
    }
    
}


