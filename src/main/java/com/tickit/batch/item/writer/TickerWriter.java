package com.tickit.batch.item.writer;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tickit.batch.domain.Ticker;
import com.tickit.batch.repository.TickerRepository;

import lombok.RequiredArgsConstructor;

@Component
@StepScope
@RequiredArgsConstructor
@Transactional
public class TickerWriter implements ItemWriter<List<Ticker>> {

	private final TickerRepository tickerRepository;

	@Override
	public void write(Chunk<? extends List<Ticker>> items) {
		items.forEach(tickerList ->
			tickerList.forEach(ticker ->
				tickerRepository.findById(ticker.getMarket())
					.ifPresentOrElse(
						existing -> existing.update(ticker),
						() -> tickerRepository.save(ticker)
					)
			)
		);
	}
}