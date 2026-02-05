package org.ptb.trackerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.ptb.trackerservice.client")
public class TrackerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackerServiceApplication.class, args);
    }
}