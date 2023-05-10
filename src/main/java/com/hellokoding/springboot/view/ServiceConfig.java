package com.hellokoding.springboot.view;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.registration.UserManagementService;
import services.storemanagement.StoreManagementService;

@Configuration
public class ServiceConfig {

    @Bean
    public UserManagementService userManagementService() {
        return new UserManagementService();
    }

    @Bean
    public StoreManagementService storeManagementService() {
        return new StoreManagementService();
    }
}
