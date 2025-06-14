package com.tickit.batch.item.reader;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.tickit.batch.domain.MarketCode;
import com.tickit.batch.repository.MarketCodeRepository;

import lombok.RequiredArgsConstructor;

@Component
@StepScope
@RequiredArgsConstructor
public class MarketCodeReader implements ItemReader<List<MarketCode>> {

	private final MarketCodeRepository repository;
	private Iterator<List<MarketCode>> groupedIterator;

	@Override
	public List<MarketCode> read() {
		if (groupedIterator == null) {
			List<MarketCode> all = repository.findAll();
			groupedIterator = IntStream.range(0, (int)Math.ceil(all.size() / 100.0))
				.mapToObj(i -> all.subList(i * 100, Math.min((i + 1) * 100, all.size())))
				.collect(Collectors.toList())
				.iterator();
		}
		return groupedIterator.hasNext() ? groupedIterator.next() : null;
	}
}