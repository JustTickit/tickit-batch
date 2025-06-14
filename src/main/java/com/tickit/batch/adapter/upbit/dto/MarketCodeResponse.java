package com.tickit.batch.adapter.upbit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MarketCodeResponse {

	private String market;

	private String korean_name;

	private String english_name;
}
