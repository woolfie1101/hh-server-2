# 콘서트 예약 서비스

## 프로젝트 문서

| 문서명 | 경로 | 설명 |
|-------|------|------|
| 프로젝트 Milestone | [`./docs/milestone.md`](./docs/milestone.md) | 9주간의 프로젝트 일정과 주차별 목표 및 마일스톤 |
| 시퀀스 다이어그램 | [`./docs/sequence.md`](./docs/sequence.md) | 주요 기능별 시스템 간 상호작용 흐름도 |
| ERD | [`./docs/erd.md`](./docs/erd.md) | 데이터베이스 테이블 구조 및 관계도 |
| 클래스 다이어그램 | [`./docs/class.md`](./docs/class.md) | 계층별 클래스 구조와 의존관계 |
| 상태 다이어그램 | [`./docs/state.md`](./docs/state.md) | 토큰, 좌석, 예약, 결제의 상태 전이도 |
| API 명세서 | [`./docs/api.md`](./docs/api.md) | REST API 엔드포인트 및 요청/응답 스펙 |
| 인프라 구성도 | [`./docs/infrastructure.md`](./docs/infrastructure.md) | Docker 기반 시스템 아키텍처 구성 |
| 요구사항 기능 목록 | [`./docs/requirements.md`](./docs/requirements.md) | 우선순위별 기능 요구사항 및 제약조건 |
| 도메인 모델 설계 | [`./docs/domain.md`](./docs/domain.md) | DDD 기반 애그리거트 및 바운디드 컨텍스트 설계 |

## 프로젝트 개요

대기열 시스템을 통한 콘서트 좌석 예약 및 결제 서비스

### 핵심 기능
- 대기열 토큰 기반 접근 제어
- 실시간 좌석 예약 시스템
- 잔액 기반 결제 처리
- 5분 임시 예약 시스템
-  동시성 제어를 통한 데이터 정합성

### 기술 스택
- **Backend**: Java 17, Spring Boot 3.x
- **Database**: MySQL 8.0
- **Cache**: Redis 7.0
- **Message Queue**: Apache Kafka
- **Test**: JUnit 5, Mockito
- **Architecture**: Layered Architecture, Clean Architecture

### 개발 기간
- **전체**: 9주 (2024.05.25 ~ 2024.07.25)
- **핵심 구현**: 2-4주차 (2024.06.01 ~ 2024.06.19)