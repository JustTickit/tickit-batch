package com.tickit.batch.listener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import com.tickit.batch.logging.TraceContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarketCodeJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		String traceId = "TRACE-" + UUID.randomUUID();
		TraceContext.setTraceId(traceId);
		jobExecution.getExecutionContext().putString("traceId", traceId);
		log.info("[MarketCodeJob] 실행 시작 - Job Name: {}, 시작 시각: {}", jobExecution.getJobInstance().getJobName(), jobExecution.getStartTime());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		LocalDateTime start = jobExecution.getStartTime();
		LocalDateTime end = jobExecution.getEndTime();

		if (start != null && end != null) {
			long elapsedMillis = Duration.between(start, end).toMillis();
			log.info("[MarketCodeJob] 실행 종료 - 상태: {}, 소요 시간: {}ms", jobExecution.getStatus(), elapsedMillis);
		} else {
			log.warn("[MarketCodeJob] 실행 시간 측정 실패 (start 또는 end가 null)");
		}

		TraceContext.clear();
	}
}