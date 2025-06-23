package com.tickit.batch.item.processor;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.tickit.batch.adapter.upbit.UpbitClient;
import com.tickit.batch.domain.MarketCode;
import com.tickit.batch.domain.Ticker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class TickerProcessor implements ItemProcessor<List<MarketCode>, List<Ticker>> {

	private final UpbitClient upbitClient;

	@Override
	public List<Ticker> process(List<MarketCode> marketCodes) {
		List<String> marketNames = marketCodes.stream()
			.map(MarketCode::getMarket)
			.toList();

		log.info("[Processor] 입력 받은 마켓 수: {}", marketNames.size());

		List<Ticker> tickers = upbitClient.getTickers(marketNames).stream()
			.map(response -> {
				try {
					return Ticker.from(response);
				} catch (IllegalArgumentException e) {
					log.warn("[Processor] Ticker 변환 실패: market = {}, reason = {}", response.getMarket(), e.getMessage());
					return null;
				}
			})
			.filter(ticker -> ticker != null)
			.toList();

		log.info("[Processor] 변환 완료: 생성된 Ticker 수 = {}", tickers.size());

		return  tickers;
	}
}