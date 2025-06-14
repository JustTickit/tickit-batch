package com.tickit.batch.job;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.tickit.batch.domain.MarketCode;
import com.tickit.batch.domain.Ticker;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TickerJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	private final ItemReader<List<MarketCode>> tickerReader;
	private final ItemProcessor<List<MarketCode>, List<Ticker>> tickerProcessor;
	private final ItemWriter<List<Ticker>> tickerWriter;

	@Bean
	public Job tickerJob() {
		return new JobBuilder("tickerJob", jobRepository)
			.start(tickerStep())
			.build();
	}

	@Bean
	public Step tickerStep() {
		return new StepBuilder("tickerStep", jobRepository)
			.<List<MarketCode>, List<Ticker>>chunk(1, transactionManager)
			.reader(tickerReader)
			.processor(tickerProcessor)
			.writer(tickerWriter)
			.allowStartIfComplete(true)
			.build();
	}
}