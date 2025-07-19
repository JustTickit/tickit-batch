package com.tickit.batch.item.writer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

import org.slf4j.MDC;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tickit.batch.domain.Ticker;
import com.tickit.batch.integrity.TickerIntegrityVerifier;
import com.tickit.batch.logging.TraceContext;
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
	private final TickerIntegrityVerifier tickerIntegrityVerifier;

	@Override
	public void write(Chunk<? extends List<Ticker>> items) {
		String traceId = TraceContext.getTraceId();
		if (traceId != null && MDC.get("traceId") == null) {
			MDC.put("traceId", traceId);
		}
		log.info("[Writer] Chunk 수신: {}개 리스트", items.size());

		AtomicInteger totalSaved = new AtomicInteger(0);
		AtomicInteger skippedDuplicates = new AtomicInteger(0);

		for (List<Ticker> tickerList : items) {
			log.info("[Writer] 처리할 Ticker 수: {}", tickerList.size());
	
			List<Ticker> uniqueTickers = new ArrayList<>();
	
			for (Ticker ticker : tickerList) {
				log.debug("[Writer] 저장 시도 - market: {}, timestamp: {}", ticker.getMarket(), ticker.getTimestamp());
	
				boolean isDuplicate = tickerRepository.existsByMarketAndTimestamp(
					ticker.getMarket(), ticker.getTimestamp());
	
				if (isDuplicate) {
					log.info("[Writer] 중복 스킵 - market: {}, timestamp: {}", ticker.getMarket(), ticker.getTimestamp());
					skippedDuplicates.incrementAndGet();
				} else {
					uniqueTickers.add(ticker);
				}
			}
	
			try {
				if (!uniqueTickers.isEmpty()) {
					List<Ticker> savedTickers = tickerRepository.saveAll(uniqueTickers);
					totalSaved.addAndGet(savedTickers.size());
	
					for (Ticker saved : savedTickers) {
						log.info("[Writer] 저장 성공 - market: {}, timestamp: {}", saved.getMarket(), saved.getTimestamp());
					}
				}
			} catch (Exception e) {
				log.error("[Writer] 배치 저장 중 오류 발생: error = {}", e.getMessage(), e);
			}
		}

		log.info("[Writer] 저장 완료 - 신규 저장: {}개, 중복 스킵: {}개", 
			totalSaved.get(), skippedDuplicates.get());

			List<Ticker> flattenedTickerList = new ArrayList<>();

			for (List<Ticker> tickerList : items) {
				flattenedTickerList.addAll(tickerList);
			}
			
			tickerIntegrityVerifier.verify(flattenedTickerList);

		MDC.remove("traceId");
	}
}