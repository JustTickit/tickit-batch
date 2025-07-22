package com.tickit.batch.item.writer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.MDC;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tickit.batch.domain.Ticker;
import com.tickit.batch.logging.TraceContext;
import com.tickit.batch.messaging.TickerProducer;
import com.tickit.batch.messaging.dto.TickerMessage;
import com.tickit.batch.repository.TickerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class TickerWriter implements ItemWriter<List<Ticker>> {

	private final TickerRepository tickerRepository;
	private final TickerProducer tickerProducer;

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

			for (Ticker ticker : tickerList) {
				try {
					Ticker saved = tickerRepository.save(ticker);
					log.info("[Writer] 저장 성공 - market: {}, timestamp: {}", saved.getMarket(), saved.getTimestamp());

					tickerProducer.send(TickerMessage.from(saved));
					totalSaved.incrementAndGet();

				} catch (DataIntegrityViolationException e) {
					log.info("[Writer] 중복 스킵 (DB 제약 위반) - market: {}, timestamp: {}", ticker.getMarket(), ticker.getTimestamp());
					skippedDuplicates.incrementAndGet();

				} catch (Exception e) {
					log.error("[Writer] 저장 중 예외 발생 - market: {}, timestamp: {}, error: {}", ticker.getMarket(), ticker.getTimestamp(), e.getMessage(), e);
				}
			}
		}

		log.info("[Writer] 저장 완료 - 신규 저장: {}개, 중복 스킵: {}개", 
			totalSaved.get(), skippedDuplicates.get());

		MDC.remove("traceId");
	}
}