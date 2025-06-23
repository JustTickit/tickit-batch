package com.tickit.batch.adapter.upbit;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.tickit.batch.adapter.upbit.dto.MarketCodeResponse;
import com.tickit.batch.adapter.upbit.dto.TickerResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitClient {

	private final WebClient webClient = WebClient.create("https://api.upbit.com");

	public List<MarketCodeResponse> getMarketCodes() {
		log.info("[Upbit] MarketCode 요청 시작");
		try {
			List<MarketCodeResponse> result = webClient.get()
				.uri("/v1/market/all?isDetails=false")
				.retrieve()
				.bodyToFlux(MarketCodeResponse.class)
				.collectList()
				.block();
			log.info("[Upbit] MarketCode 요청 성공: 수신된 마켓 수 = {}", result.size());
			return result;
		} catch (Exception e){
			log.error("[Upbit] MarketCode 요청 실패: {}", e.getMessage(), e);
			throw e;
		}
	}

	public List<TickerResponse> getTickers(List<String> markets) {
		String marketParam = String.join(",", markets);
		log.info("[Upbit] Ticker 요청 시작: 요청 마켓 수 = {}", markets.size());
		try {
			List<TickerResponse> result = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/v1/ticker")
					.queryParam("markets", marketParam)
					.build())
				.retrieve()
				.bodyToFlux(TickerResponse.class)
				.collectList()
				.block();
			log.info("[Upbit] Ticker 요청 성공: 응답 받은 티커 수 = {}", result.size());
			return result;
		} catch (Exception e){
			log.error("[Upbit] Ticker 요청 실패: {}", e.getMessage(), e);
			throw e;
		}
	}
}