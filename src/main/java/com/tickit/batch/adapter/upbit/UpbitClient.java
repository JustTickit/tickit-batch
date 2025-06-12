package com.tickit.batch.adapter.upbit;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.tickit.batch.adapter.upbit.dto.MarketCodeResponse;

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
}
