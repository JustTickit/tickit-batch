package com.tickit.batch.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tickit.batch.domain.Ticker;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TickerMessage {
    private String market;
    private BigDecimal tradePrice;
    private BigDecimal signedChangePrice;
    private BigDecimal signedChangeRate;
    private BigDecimal accTradeVolume;
    private LocalDateTime timestamp;

    @Builder
	public TickerMessage(String market, BigDecimal tradePrice, LocalDateTime timestamp) {
		this.market = market;
		this.tradePrice = tradePrice;
		this.timestamp = timestamp;
	}

    public Ticker toEntity() {
        return Ticker.of(
            this.market,
            this.tradePrice,
            this.signedChangePrice != null ? this.signedChangePrice : BigDecimal.ZERO,
            this.signedChangeRate != null ? this.signedChangeRate : BigDecimal.ZERO,
            this.accTradeVolume != null ? this.accTradeVolume : BigDecimal.ZERO,
            this.timestamp
        );
    }

	public static TickerMessage from(Ticker ticker) {
		return TickerMessage.builder()
			.market(ticker.getMarket())
			.tradePrice(ticker.getTradePrice())
			.timestamp(ticker.getTimestamp())
			.build();
	}
}
