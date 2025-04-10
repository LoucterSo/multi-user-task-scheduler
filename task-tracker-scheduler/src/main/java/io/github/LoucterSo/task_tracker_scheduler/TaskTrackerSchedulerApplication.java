package io.github.LoucterSo.task_tracker_scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
public class TaskTrackerSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerSchedulerApplication.class, args);
	}

}
