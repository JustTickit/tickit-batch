package com.tickit.batch.adapter.upbit;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpbitClient {

	private final WebClient webClient = WebClient.create("https://api.upbit.com");
}
