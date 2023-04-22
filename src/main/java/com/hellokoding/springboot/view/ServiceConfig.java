package com.hellokoding.springboot.view;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.registration.UserManagementService;

@Configuration
public class ServiceConfig {

    @Bean
    public UserManagementService userManagementService() {
        return new UserManagementService();
    }
}
