package com.tickit.batch.integrity;

import com.tickit.batch.domain.Ticker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TickerIntegrityVerifier {

    private static final int TIMESTAMP_TOLERANCE_SECONDS = 2; 

    public void verify(List<Ticker> tickers) {
        if (tickers.isEmpty()) {
            log.warn("[IntegrityCheck] 저장된 Ticker가 없습니다.");
            return;
        }

        LocalDateTime referenceTimestamp = tickers.stream()
            .map(Ticker::getTimestamp)
            .max(LocalDateTime::compareTo)
            .orElseThrow();

        List<Ticker> validTickers = tickers.stream()
            .filter(ticker -> Math.abs(ChronoUnit.SECONDS.between(ticker.getTimestamp(), referenceTimestamp)) <= TIMESTAMP_TOLERANCE_SECONDS)
            .toList();

        Set<String> savedMarkets = validTickers.stream()
            .map(Ticker::getMarket)
            .collect(Collectors.toSet());

        int expectedMarketCount = tickers.size(); 
        int actualMarketCount = savedMarkets.size();

        log.info("[IntegrityCheck] 기준 timestamp: {}", referenceTimestamp);
        log.info("[IntegrityCheck] 저장된 마켓 수: {} / 기대 마켓 수: {}", actualMarketCount, expectedMarketCount);

        if (actualMarketCount < expectedMarketCount) {
            Set<String> allMarkets = tickers.stream().map(Ticker::getMarket).collect(Collectors.toSet());
            allMarkets.removeAll(savedMarkets);

            log.warn("[IntegrityCheck] 누락된 마켓 코드 수: {}", expectedMarketCount - actualMarketCount);
            log.warn("[IntegrityCheck] 누락된 마켓 코드: {}", String.join(", ", allMarkets));
        }
    }
}