package com.tickit.batch.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}