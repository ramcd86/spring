package com.tradr.springboot.view;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.registration.UserManagementService;
import services.resourceprocessor.ImageProcessorService;
import services.search.SearchManagementService;
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

	@Bean
	public ImageProcessorService imageProcessorService() {
		return new ImageProcessorService();
	}

	@Bean
	public SearchManagementService searchManagementService() {
		return new SearchManagementService();
	}
}
