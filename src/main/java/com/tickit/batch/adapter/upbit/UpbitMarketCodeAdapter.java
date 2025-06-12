package com.tickit.batch.adapter.upbit;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tickit.batch.adapter.upbit.dto.MarketCodeResponse;
import com.tickit.batch.domain.MarketCode;

@Component
public class UpbitMarketCodeAdapter {

	public List<MarketCode> convertToDomain(List<MarketCodeResponse> responses) {
		return responses.stream()
			.map(res -> MarketCode.of(
				res.getMarket(),
				res.getKorean_name(),
				res.getEnglish_name()
			)).toList();
	}
}
