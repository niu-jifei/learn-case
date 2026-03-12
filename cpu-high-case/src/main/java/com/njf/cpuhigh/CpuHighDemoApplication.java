package com.njf.cpuhigh;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CpuHighDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CpuHighDemoApplication.class, args);
    }
}
