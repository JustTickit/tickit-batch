package com.tickit.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tickit.batch.domain.Ticker;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {
}
