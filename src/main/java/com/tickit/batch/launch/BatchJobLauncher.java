package com.tickit.batch.launch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobLauncher {

	private final JobLauncher jobLauncher;

	@Qualifier("marketCodeJob") 
	private final Job marketCodeJob;

	@Bean
	public CommandLineRunner runMarketCodeJob() {
		return args -> {
			try {
				log.info("[Launcher] MarketCodeJob 실행 시작");

				JobParameters params = new JobParametersBuilder()
					.addLong("run.id", System.currentTimeMillis())
					.toJobParameters();

				jobLauncher.run(marketCodeJob, params);
				
				log.info("[Launcher] MarketCodeJob 실행 완료");
			} catch (Exception e) {
				log.error("[Launcher] MarketCodeJob 실행 실패: ", e);
			}
		};
	}
}