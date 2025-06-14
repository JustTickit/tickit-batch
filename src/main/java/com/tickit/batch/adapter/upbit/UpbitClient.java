package com.tickit.batch.adapter.upbit;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.tickit.batch.adapter.upbit.dto.MarketCodeResponse;
import com.tickit.batch.adapter.upbit.dto.TickerResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpbitClient {

	private final WebClient webClient = WebClient.create("https://api.upbit.com");

	public List<MarketCodeResponse> getMarketCodes() {
		return webClient.get()
			.uri("/v1/market/all?isDetails=false")
			.retrieve()
			.bodyToFlux(MarketCodeResponse.class)
			.collectList()
			.block();
	}

	public List<TickerResponse> getTickers(List<String> markets) {
		String marketParam = String.join(",", markets);
		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/v1/ticker")
				.queryParam("markets", marketParam)
				.build())
			.retrieve()
			.bodyToFlux(TickerResponse.class)
			.collectList()
			.block();
	}
}