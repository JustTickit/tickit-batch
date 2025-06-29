package com.tickit.batch.listener;

import org.slf4j.MDC;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickerStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		String traceId = stepExecution.getJobExecution()
			.getExecutionContext()
			.getString("traceId");

		if (traceId != null) {
			MDC.put("traceId", traceId);
		}

		log.info("[TickerStep] Step 시작: {}", stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("[TickerStep] Step 종료 - ReadCount: {}, WriteCount: {}, ExitStatus: {}, Status: {}",
			stepExecution.getReadCount(),
			stepExecution.getWriteCount(),
			stepExecution.getExitStatus(),
			stepExecution.getStatus());

		MDC.remove("traceId");

		return stepExecution.getExitStatus();
	}
}