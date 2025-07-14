package com.tickit.batch.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tickit.batch.domain.Ticker;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {

	boolean existsByMarketAndTimestamp(String market, LocalDateTime timestamp);
}
