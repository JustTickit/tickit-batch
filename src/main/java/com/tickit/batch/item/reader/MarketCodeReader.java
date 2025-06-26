package com.tickit.batch.item.reader;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.tickit.batch.domain.MarketCode;
import com.tickit.batch.logging.TraceContext;
import com.tickit.batch.repository.MarketCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class MarketCodeReader implements ItemReader<List<MarketCode>> {

	private final MarketCodeRepository repository;
	private Iterator<List<MarketCode>> groupedIterator;
	private int currentChunkIndex = 0;

	@Override
	public List<MarketCode> read() {
		if (groupedIterator == null) {
			List<MarketCode> all = repository.findAll();

			// log.info("[Reader] 전체 마켓코드 수: {}", all.size());
			log.info("[{}] [Reader] 전체 마켓코드 수: {}", TraceContext.getTraceId(), all.size());

			List<List<MarketCode>> grouped = IntStream.range(0, (int)Math.ceil(all.size() / 100.0))
				.mapToObj(i -> all.subList(i * 100, Math.min((i + 1) * 100, all.size())))
				.collect(Collectors.toList());

			// log.info("[Reader] 100개 단위로 나눈 그룹 수: {}", grouped.size());
			log.info("[{}][Reader] 100개 단위로 나눈 그룹 수: {}", TraceContext.getTraceId(), grouped.size());

			groupedIterator = grouped.iterator();
		}

		if (groupedIterator.hasNext()) {
			List<MarketCode> nextChunk = groupedIterator.next();
			// log.info("[Reader] {}번째 마켓코드 그룹 읽음 (크기: {})", ++currentChunkIndex, nextChunk.size());
			log.info("[{}] [Reader] {}번째 마켓코드 그룹 읽음 (크기: {})", TraceContext.getTraceId(), ++currentChunkIndex, nextChunk.size());
			return nextChunk;
		} else {
			// log.info("[Reader] 모든 마켓코드 그룹을 읽음, 종료.");
			log.info("[{}] [Reader] 모든 마켓코드 그룹을 읽음, 종료.", TraceContext.getTraceId());
			return null;
		}
	}
}