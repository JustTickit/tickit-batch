package com.tickit.batch.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tickit.batch")
public class BatchJobLauncher {

	public static void main(String[] args) {
		SpringApplication.run(BatchJobLauncher.class, args);
	}
}
