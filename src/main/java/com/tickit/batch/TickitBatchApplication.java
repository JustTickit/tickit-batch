package com.tickit.batch;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@EnableRabbit
public class TickitBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickitBatchApplication.class, args);
	}
}
