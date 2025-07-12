package com.tickit.batch.item.reader;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.tickit.batch.domain.MarketCode;
import com.tickit.batch.repository.MarketCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class MarketCodeReader implements ItemReader<List<MarketCode>> {

	private final MarketCodeRepository repository;
	private boolean hasRead = false;

	@Override
	public List<MarketCode> read() {
		if (hasRead) {
			log.info("[Reader] 이미 모든 마켓코드를 읽음, 종료.");
			return null;
		}

		List<MarketCode> allMarketCodes = repository.findAll();
		log.info("[Reader] 전체 마켓코드 수: {}", allMarketCodes.size());

		hasRead = true;
		
		return allMarketCodes;
	}
}