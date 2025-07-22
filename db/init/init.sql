-- =====================================================
-- Tickit Batch Application Database Schema
-- =====================================================

-- 데이터베이스 생성 및 선택
CREATE DATABASE IF NOT EXISTS tickit;
USE tickit;

-- =====================================================
-- 애플리케이션 테이블
-- =====================================================

-- 암호화폐 티커 정보 저장 테이블
-- Upbit API에서 수집한 실시간 가격 정보를 저장
CREATE TABLE IF NOT EXISTS ticker (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    market VARCHAR(20) NOT NULL,
    trade_price DECIMAL(20,6) NOT NULL,
    signed_change_price DECIMAL(20,6),
    signed_change_rate DECIMAL(20,6),
    acc_trade_volume DECIMAL(20,6),
    timestamp DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 중복 방지를 위한 unique index
    UNIQUE KEY uk_market_timestamp (market, timestamp),
    
    -- 조회 성능을 위한 index
    INDEX idx_market (market),
    INDEX idx_timestamp (timestamp),
    INDEX idx_market_timestamp (market, timestamp)
);

-- =====================================================
-- Spring Batch 메타데이터 테이블
-- =====================================================

-- 암호화폐 마켓 코드 정보 저장 테이블
-- Upbit에서 제공하는 거래 가능한 마켓 목록을 저장
CREATE TABLE IF NOT EXISTS market_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    market VARCHAR(20) NOT NULL UNIQUE,
    korean_name VARCHAR(100),
    english_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- Spring Batch 메타데이터 테이블
-- =====================================================

-- Spring Batch 작업 인스턴스 정보 저장
-- 각 배치 작업의 고유 식별자와 메타데이터를 관리
CREATE TABLE IF NOT EXISTS BATCH_JOB_INSTANCE (
    JOB_INSTANCE_ID BIGINT NOT NULL PRIMARY KEY,
    VERSION BIGINT,
    JOB_NAME VARCHAR(100) NOT NULL,
    JOB_KEY VARCHAR(32) NOT NULL,
    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ENGINE=InnoDB;

-- Spring Batch 작업 실행 정보 저장
-- 배치 작업의 실행 상태, 시작/종료 시간, 결과 등을 관리
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION (
    JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    VERSION BIGINT,
    JOB_INSTANCE_ID BIGINT NOT NULL,
    CREATE_TIME DATETIME(6) NOT NULL,
    START_TIME DATETIME(6) DEFAULT NULL,
    END_TIME DATETIME(6) DEFAULT NULL,
    STATUS VARCHAR(10),
    EXIT_CODE VARCHAR(2500),
    EXIT_MESSAGE VARCHAR(2500),
    LAST_UPDATED DATETIME(6),
    constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
    references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ENGINE=InnoDB;

-- Spring Batch 작업 실행 파라미터 저장
-- 배치 작업 실행 시 전달된 파라미터 정보를 관리
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_PARAMS (
    JOB_EXECUTION_ID BIGINT NOT NULL,
    PARAMETER_NAME VARCHAR(100) NOT NULL,
    PARAMETER_TYPE VARCHAR(100) NOT NULL,
    PARAMETER_VALUE VARCHAR(2500),
    IDENTIFYING CHAR(1) NOT NULL,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
    references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- Spring Batch 스텝 실행 정보 저장
-- 배치 작업 내 각 스텝의 실행 상태와 처리 결과를 관리
CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION (
    STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    VERSION BIGINT NOT NULL,
    STEP_NAME VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID BIGINT NOT NULL,
    CREATE_TIME DATETIME(6) NOT NULL,
    START_TIME DATETIME(6) DEFAULT NULL,
    END_TIME DATETIME(6) DEFAULT NULL,
    STATUS VARCHAR(10),
    COMMIT_COUNT BIGINT,
    READ_COUNT BIGINT,
    FILTER_COUNT BIGINT,
    WRITE_COUNT BIGINT,
    READ_SKIP_COUNT BIGINT,
    WRITE_SKIP_COUNT BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT BIGINT,
    EXIT_CODE VARCHAR(2500),
    EXIT_MESSAGE VARCHAR(2500),
    LAST_UPDATED DATETIME(6),
    constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
    references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- Spring Batch 작업 실행 컨텍스트 저장
-- 배치 작업 실행 중 공유되는 데이터와 상태 정보를 관리
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_CONTEXT (
    JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
    references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- Spring Batch 스텝 실행 컨텍스트 저장
-- 배치 스텝 실행 중 공유되는 데이터와 상태 정보를 관리
CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_CONTEXT (
    STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
    references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ENGINE=InnoDB;

-- =====================================================
-- Spring Batch 시퀀스 테이블
-- =====================================================

-- Spring Batch에서 사용하는 시퀀스 테이블들
-- 각 테이블의 ID 자동 증가를 위한 시퀀스 관리
CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ (ID BIGINT NOT NULL) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ (ID BIGINT NOT NULL) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS BATCH_JOB_SEQ (ID BIGINT NOT NULL) ENGINE=InnoDB;

-- 시퀀스 테이블 초기화 (초기값 0으로 설정)
INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID) SELECT * FROM (SELECT 0 AS ID) AS tmp WHERE NOT EXISTS (SELECT * FROM BATCH_STEP_EXECUTION_SEQ);
INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID) SELECT * FROM (SELECT 0 AS ID) AS tmp WHERE NOT EXISTS (SELECT * FROM BATCH_JOB_EXECUTION_SEQ);
INSERT INTO BATCH_JOB_SEQ (ID) SELECT * FROM (SELECT 0 AS ID) AS tmp WHERE NOT EXISTS (SELECT * FROM BATCH_JOB_SEQ);