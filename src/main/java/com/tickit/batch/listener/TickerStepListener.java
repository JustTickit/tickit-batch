package com.tickit.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import com.tickit.batch.logging.TraceContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickerStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// log.info("[TickerStep] Step 시작: {}", stepExecution.getStepName());
		log.info("[{}}] [TickerStep] Step 시작: {}", TraceContext.getTraceId(), stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// log.info("[TickerStep] Step 종료 - ReadCount: {}, WriteCount: {}, ExitStatus: {}, Status: {}",
		//     stepExecution.getReadCount(),
		//     stepExecution.getWriteCount(),
		//     stepExecution.getExitStatus(),
		//     stepExecution.getStatus());
		log.info("[{}}] [TickerStep] Step 종료 - ReadCount: {}, WriteCount: {}, ExitStatus: {}, Status: {}",
			TraceContext.getTraceId(),
			stepExecution.getReadCount(),
			stepExecution.getWriteCount(),
			stepExecution.getExitStatus(),
			stepExecution.getStatus());

		return stepExecution.getExitStatus();
	}
}