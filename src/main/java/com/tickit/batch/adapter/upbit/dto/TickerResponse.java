package com.tickit.batch.adapter.upbit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickerResponse {

	@JsonProperty("market")
	private String market;

	@JsonProperty("trade_price")
	private Double tradePrice;

	@JsonProperty("signed_change_price")
	private Double signedChangePrice;

	@JsonProperty("signed_change_rate")
	private Double signedChangeRate;

	@JsonProperty("acc_trade_volume")
	private Double accTradeVolume;

	@JsonProperty("timestamp")
	private Long timestamp;
}
