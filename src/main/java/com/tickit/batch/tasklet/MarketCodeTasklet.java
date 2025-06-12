package com.tickit.batch.tasklet;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.tickit.batch.adapter.upbit.UpbitClient;
import com.tickit.batch.adapter.upbit.UpbitMarketCodeAdapter;
import com.tickit.batch.adapter.upbit.dto.MarketCodeResponse;
import com.tickit.batch.domain.MarketCode;
import com.tickit.batch.repository.MarketCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class MarketCodeTasklet implements Tasklet {

	private final UpbitClient upbitClient;
	private final UpbitMarketCodeAdapter adapter;
	private final MarketCodeRepository repository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

		List<MarketCodeResponse> responses = upbitClient.getMarketCodes();

		List<MarketCode> domainObjects = adapter.convertToDomain(responses);

		repository.saveAll(domainObjects);

		return RepeatStatus.FINISHED;
	}
}
