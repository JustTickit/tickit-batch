package com.tickit.batch.listener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import com.tickit.batch.logging.TraceContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickerJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		String traceId = "TRACE-" + UUID.randomUUID();
		TraceContext.setTraceId(traceId);
		jobExecution.getExecutionContext().putString("traceId", traceId);
		log.info("[TickerJob] 실행 시작 - run.id: {}", jobExecution.getJobParameters().getLong("run.id"));
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		LocalDateTime start = jobExecution.getStartTime();
		LocalDateTime end = jobExecution.getEndTime();

		if (start != null && end != null) {
			long elapsed = Duration.between(start, end).toMillis();
			log.info("[TickerJob] 실행 종료 - 상태: {}, 소요 시간: {}ms", jobExecution.getStatus(), elapsed);
		} else {
			log.warn("[TickerJob] 종료 로그 기록 실패 (startTime or endTime is null)");
		}

		TraceContext.clear();
	}
}