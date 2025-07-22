package com.tickit.batch.messaging;

import com.tickit.batch.domain.Ticker;
import com.tickit.batch.messaging.dto.TickerMessage;
import com.tickit.batch.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TickerConsumer {

    private final TickerRepository tickerRepository;

    @RabbitListener(queues = "ticker.queue", containerFactory = "rabbitListenerContainerFactory")
    public void receive(TickerMessage message) {
        log.debug("[Consumer] 메시지 수신 - market: {}, timestamp: {}", message.getMarket(), message.getTimestamp());

        try {
            Ticker ticker = message.toEntity(); 
            tickerRepository.save(ticker);
            log.info("[Consumer] 저장 완료 - market: {}, timestamp: {}", ticker.getMarket(), ticker.getTimestamp());
        } catch (Exception e) {
            log.error("[Consumer] 저장 실패 - market: {}, error: {}", message.getMarket(), e.getMessage(), e);
        }
    }
}