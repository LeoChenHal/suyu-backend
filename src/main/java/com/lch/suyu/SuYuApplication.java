package com.lch.suyu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lch
 */
@SpringBootApplication
@EnableScheduling
public class SuYuApplication {

    public static void main(String[] args) {

        SpringApplication.run(SuYuApplication.class, args);
    }

}
