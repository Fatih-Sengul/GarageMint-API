package com.api.garagemint.garagemintapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

record HelloResponse(String app, String status, String time) {}

@RestController
@RequestMapping("/api")
public class HelloController {

    @Value("${app.name:GarageMint API}")
    private String appName;

    @GetMapping("/hello")
    public HelloResponse hello() {
        return new HelloResponse(appName, "ok", Instant.now().toString());
    }
}
