package io.github.LoucterSo.task_tracker_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TaskTrackerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerBackendApplication.class, args);
    }

}
