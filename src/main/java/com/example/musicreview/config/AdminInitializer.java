package com.example.musicreview.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.musicreview.service.UserService;

@Configuration
public class AdminInitializer {

    @Bean
    ApplicationRunner ensureAdminUser(UserService userService) {
        return args -> userService.ensureAdminUserExists();
    }
}
