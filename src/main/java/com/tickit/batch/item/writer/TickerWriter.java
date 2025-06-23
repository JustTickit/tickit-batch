package com.tickit.batch.item.writer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tickit.batch.domain.Ticker;
import com.tickit.batch.repository.TickerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
@Transactional
public class TickerWriter implements ItemWriter<List<Ticker>> {

	private final TickerRepository tickerRepository;

	@Override
	public void write(Chunk<? extends List<Ticker>> items) {
		log.info("[Writer] Chunk 수신: {}개 리스트", items.size());

		AtomicInteger totalSaved = new AtomicInteger(0);
		AtomicInteger totalUpdated = new AtomicInteger(0);

		for(List<Ticker> tickerList : items){
			log.info("[Writer] 처리할 Ticker 수: {}", tickerList.size());

			for (Ticker ticker : tickerList){
				try {
					tickerRepository.findById(ticker.getMarket())
						.ifPresentOrElse(
							existing -> {
								existing.update(ticker);
								totalUpdated.incrementAndGet();
							},
							() -> {
								tickerRepository.save(ticker);
								totalSaved.incrementAndGet();							}
						);
				} catch (Exception e){
					log.error("[Writer] Ticker 저장 중 오류 발생: market = {}, error = {}", ticker.getMarket(), e.getMessage(), e);
				}
			}
		}

		log.info("[Writer] 저장 완료 - 신규 저장: {}개, 기존 업데이트: {}개", totalSaved, totalUpdated);
	}
}