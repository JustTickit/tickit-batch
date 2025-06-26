package com.tickit.batch.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.tickit.batch.adapter.upbit.dto.TickerResponse;
import com.tickit.batch.logging.TraceContext;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "ticker")
@Getter
@NoArgsConstructor
public class Ticker {

	@Id
	@Column(name = "market", nullable = false, length = 20)
	private String market;

	@Column(name = "trade_price", precision = 20, scale = 6, nullable = false)
	private BigDecimal tradePrice;

	@Column(name = "signed_change_price", precision = 20, scale = 6)
	private BigDecimal signedChangePrice;

	@Column(name = "signed_change_rate", precision = 20, scale = 6)
	private BigDecimal signedChangeRate;

	@Column(name = "acc_trade_volume", precision = 20, scale = 6)
	private BigDecimal accTradeVolume;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	private Ticker(
		String market,
		BigDecimal tradePrice,
		BigDecimal signedChangePrice,
		BigDecimal signedChangeRate,
		BigDecimal accTradeVolume,
		LocalDateTime timestamp
	) {
		this.market = market;
		this.tradePrice = tradePrice;
		this.signedChangePrice = signedChangePrice;
		this.signedChangeRate = signedChangeRate;
		this.accTradeVolume = accTradeVolume;
		this.timestamp = timestamp;
	}

	public static Ticker of(
		String market,
		BigDecimal tradePrice,
		BigDecimal signedChangePrice,
		BigDecimal signedChangeRate,
		BigDecimal accTradeVolume,
		LocalDateTime timestamp
	) {
		return new Ticker(market, tradePrice, signedChangePrice, signedChangeRate, accTradeVolume, timestamp);
	}

	public static Ticker from(TickerResponse response) {
		if (response.getTradePrice() == null) {
			throw new IllegalArgumentException(
				"마켓 코드 [" + response.getMarket() + "]에 대한 거래 가격(trade_price)이 null 입니다.");
		}

		return Ticker.of(
			response.getMarket(),
			BigDecimal.valueOf(response.getTradePrice()),
			BigDecimal.valueOf(response.getSignedChangePrice() != null ? response.getSignedChangePrice() : 0),
			BigDecimal.valueOf(response.getSignedChangeRate() != null ? response.getSignedChangeRate() : 0),
			BigDecimal.valueOf(response.getAccTradeVolume() != null ? response.getAccTradeVolume() : 0),
			Instant.ofEpochMilli(response.getTimestamp())
				.atZone(ZoneId.of("Asia/Seoul"))
				.toLocalDateTime()
		);
	}

	public void update(Ticker newTicker) {
		boolean changed = false;

		if (this.tradePrice.compareTo(newTicker.tradePrice) != 0) {
			log.info("""
					[{}]
					[Writer] 시세 변동
					  마켓: {}
					  tradePrice: {} → {}
					""",
				TraceContext.getTraceId(), market,
				this.tradePrice.toPlainString(), newTicker.tradePrice.toPlainString());
			changed = true;
		}
		if (this.signedChangePrice.compareTo(newTicker.signedChangePrice) != 0) {
			log.info("""
					[{}]
					[Writer] 시세 변동
					마켓: {}
					signedChangePrice: {} → {}
					""", TraceContext.getTraceId(), market,
				this.signedChangePrice.toPlainString(), newTicker.signedChangePrice.toPlainString());
			changed = true;
		}
		if (this.signedChangeRate.compareTo(newTicker.signedChangeRate) != 0) {
			log.info("""
					[{}]
					[Writer] 시세 변동
					마켓: {}
					signedChangeRate: {} → {}
					""", TraceContext.getTraceId(), market,
				this.signedChangeRate.toPlainString(), newTicker.signedChangeRate.toPlainString());
			changed = true;
		}
		if (this.accTradeVolume.compareTo(newTicker.accTradeVolume) != 0) {
			log.info("""
					[{}]
					[Writer] 시세 변동
					마켓: {}
					accTradeVolume: {} → {}
					""", TraceContext.getTraceId(), market,
				this.accTradeVolume.toPlainString(), newTicker.accTradeVolume.toPlainString());
			changed = true;
		}
		if (this.timestamp.compareTo(newTicker.timestamp) != 0) {
			log.info("""
					[{}]
					[Writer] 시세 변동
					마켓: {}
					timestamp: {} → {}
					""", TraceContext.getTraceId(), market,
				this.timestamp, newTicker.timestamp);
			changed = true;
		}

		if (!changed) {
			log.debug("""
				[{}]
				[Writer]
				마켓 {}: 시세 데이터에 변화 없음
				""", TraceContext.getTraceId(), market);
		}

		this.tradePrice = newTicker.tradePrice;
		this.signedChangePrice = newTicker.signedChangePrice;
		this.signedChangeRate = newTicker.signedChangeRate;
		this.accTradeVolume = newTicker.accTradeVolume;
		this.timestamp = newTicker.timestamp;
	}
}