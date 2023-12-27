package com.seulah.los;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LosApplication {

    public static void main(String[] args) {
        SpringApplication.run(LosApplication.class, args);
    }

}
