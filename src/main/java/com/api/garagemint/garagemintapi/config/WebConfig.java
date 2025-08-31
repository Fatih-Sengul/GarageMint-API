package com.api.garagemint.garagemintapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.uploads.root}")
    private String uploadsRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // http://localhost:8080/uploads/** -> {uploadsRoot}/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + (uploadsRoot.endsWith("/") ? uploadsRoot : uploadsRoot + "/"));
    }
}
