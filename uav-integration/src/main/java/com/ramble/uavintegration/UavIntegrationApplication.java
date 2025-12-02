package com.ramble.uavintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ComponentScan(basePackages = {"com.ramble"})
@SpringBootApplication
public class UavIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(UavIntegrationApplication.class, args);

    }

}
