package com.tradr.springboot.view;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.registration.UserManagementService;
import services.storemanagement.StoreManagementService;
import services.utils.ImageProcessorService;

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

    @Bean
    public ImageProcessorService imageProcessorService() {
        return new ImageProcessorService();
    }
}
