package com.tickit.batch.item.processor;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.tickit.batch.adapter.upbit.UpbitClient;
import com.tickit.batch.domain.MarketCode;
import com.tickit.batch.domain.Ticker;

import lombok.RequiredArgsConstructor;

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

		return upbitClient.getTickers(marketNames).stream()
			.map(response -> {
				try {
					return Ticker.from(response);
				} catch (IllegalArgumentException e) {
					return null;
				}
			})
			.filter(ticker -> ticker != null)
			.toList();
	}
}