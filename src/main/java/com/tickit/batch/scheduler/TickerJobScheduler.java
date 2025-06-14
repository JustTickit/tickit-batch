package com.tickit.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TickerJobScheduler {

	private final JobLauncher jobLauncher;
	private final Job tickerJob;

	@Scheduled(fixedRate = 1000)
	public void runTickerJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("run.id", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(tickerJob, jobParameters);
		} catch (Exception e) {
			log.error("TickerJob 실행 실패: ", e);
		}
	}
}