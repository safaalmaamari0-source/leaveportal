package com.agileoracles.leave_portal_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeavePortalAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeavePortalAppApplication.class, args);
	}

}
