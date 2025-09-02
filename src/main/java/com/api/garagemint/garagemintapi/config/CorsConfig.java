package com.api.garagemint.garagemintapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
            .allowCredentials(true);

        registry.addMapping("/api/v1/profiles/*/follow")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
            .allowCredentials(true);

        registry.addMapping("/api/v1/profiles/*/followers")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
            .allowCredentials(true);

        registry.addMapping("/api/v1/profiles/*/following")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
            .allowCredentials(true);
      }
    };
  }
}
