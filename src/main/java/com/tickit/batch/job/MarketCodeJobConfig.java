package com.tickit.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MarketCodeJobConfig {

	@Bean
	public Job marketCodeJob(JobRepository jobRepository, Step marketCodeStep) {
		return new JobBuilder("marketCodeJob", jobRepository)
			.start(marketCodeStep)
			.build();
	}

	@Bean
	public Step marketCodeStep(
		JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet marketCodeTasklet) {
		return new StepBuilder("marketCodeStep", jobRepository)
			.tasklet(marketCodeTasklet, transactionManager)
			.build();
	}
}
