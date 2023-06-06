package com.tradr.springboot.view;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import services.utils.DatabaseVerification;

import java.sql.SQLException;

@SpringBootApplication
@EnableAutoConfiguration
public class Application {


    public static void main(String[] args) throws SQLException {

        SpringApplication.run(Application.class, args);

        DatabaseVerification.databaseVerification()
                .validateUserTable()
                .validateStoreTable()
                .validateStoreItems()
                .validateStoreReviews();

    }
}
