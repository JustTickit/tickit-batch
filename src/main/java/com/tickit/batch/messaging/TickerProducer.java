package com.tickit.batch.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.tickit.batch.messaging.dto.TickerMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class TickerProducer {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "ticker.exchange";
    private static final String ROUTING_KEY = "ticker.key";

    public void send(TickerMessage message) {
        log.warn("[PRODUCER-TEST] 발행 요청 - market: {}, timestamp: {}", message.getMarket(), message.getTimestamp()); 
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
    }
}