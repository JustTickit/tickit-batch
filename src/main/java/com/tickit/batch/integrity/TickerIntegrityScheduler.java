package com.tickit.batch.integrity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TickerIntegrityScheduler {

    private final TickerIntegrityVerifier tickerIntegrityVerifier;

    @Scheduled(fixedDelay = 1000)
    public void verifyLatest() {
        LocalDateTime target = LocalDateTime.now()
            .minusSeconds(2)
            .truncatedTo(ChronoUnit.SECONDS);

        log.info("[IntegrityScheduler] {} 시점 정합성 검증 시작", target);
        tickerIntegrityVerifier.verifyByTimestamp(target);
    }
}