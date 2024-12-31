package com.xxx.test.api.nrt.apinrt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.xxx.test.api")
public class ApiNrtApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiNrtApplication.class, args);
    }

}
