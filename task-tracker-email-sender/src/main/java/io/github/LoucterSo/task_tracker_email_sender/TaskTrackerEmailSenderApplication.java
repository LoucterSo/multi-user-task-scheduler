package io.github.LoucterSo.task_tracker_email_sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TaskTrackerEmailSenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerEmailSenderApplication.class, args);
	}

}
