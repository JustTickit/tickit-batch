package com.tickit.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tickit.batch.domain.MarketCode;

public interface MarketCodeRepository extends JpaRepository<MarketCode, Long> {
}
