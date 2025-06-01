# 콘서트 예약 서비스 상태 다이어그램

## 1. 대기열 토큰 상태 다이어그램

```mermaid
stateDiagram-v2
    [*] --> WAITING : 토큰 발급 요청
    
    WAITING --> ACTIVE : 대기열 순서 도달
    WAITING --> EXPIRED : 대기 시간 초과
    
    ACTIVE --> EXPIRED : 토큰 사용 완료 (결제 성공)
    ACTIVE --> EXPIRED : 활성 시간 만료 (30분)
    
    EXPIRED --> [*] : 토큰 삭제
    
    note right of WAITING
        - 대기열에서 순서 대기
        - 폴링으로 상태 확인
        - 최대 대기 시간 제한
    end note
    
    note right of ACTIVE
        - 예약/결제 가능 상태
        - 30분 활성 시간 제한
        - 결제 완료 시 만료
    end note
    
    note right of EXPIRED
        - 더 이상 사용 불가
        - 정리 대상
    end note
```

## 2. 좌석 상태 다이어그램

```mermaid
stateDiagram-v2
    [*] --> AVAILABLE : 좌석 생성
    
    AVAILABLE --> TEMP_RESERVED : 좌석 예약 요청
    AVAILABLE --> AVAILABLE : 예약 실패 (동시성)
    
    TEMP_RESERVED --> RESERVED : 결제 완료
    TEMP_RESERVED --> AVAILABLE : 5분 임시 예약 만료
    TEMP_RESERVED --> AVAILABLE : 예약 취소
    
    RESERVED --> RESERVED : 최종 상태
    
    note right of AVAILABLE
        - 예약 가능한 상태
        - 다른 사용자가 예약 시도 가능
    end note
    
    note right of TEMP_RESERVED
        - 5분간 임시 배정
        - 해당 사용자만 결제 가능
        - 다른 사용자 접근 차단
    end note
    
    note right of RESERVED
        - 결제 완료된 최종 상태
        - 변경 불가능
    end note
```

## 3. 예약 상태 다이어그램

```mermaid
stateDiagram-v2
    [*] --> TEMP_RESERVED : 좌석 예약 성공
    
    TEMP_RESERVED --> CONFIRMED : 결제 완료
    TEMP_RESERVED --> EXPIRED : 5분 시간 만료
    TEMP_RESERVED --> CANCELLED : 사용자 취소
    
    CONFIRMED --> CONFIRMED : 최종 상태
    EXPIRED --> [*] : 예약 정리
    CANCELLED --> [*] : 예약 정리
    
    note right of TEMP_RESERVED
        - 5분간 유효한 임시 예약
        - 결제 대기 상태
        - 자동 만료 스케줄링
    end note
    
    note right of CONFIRMED
        - 결제 완료된 확정 예약
        - 콘서트 참석 가능
    end note
    
    note right of EXPIRED
        - 시간 초과로 자동 만료
        - 좌석 해제됨
    end note
```

## 4. 결제 상태 다이어그램

```mermaid
stateDiagram-v2
    [*] --> PROCESSING : 결제 요청
    
    PROCESSING --> SUCCESS : 잔액 충분 & 처리 성공
    PROCESSING --> FAILED : 잔액 부족
    PROCESSING --> FAILED : 예약 만료
    PROCESSING --> FAILED : 시스템 오류
    
    SUCCESS --> SUCCESS : 최종 상태
    FAILED --> CANCELLED : 관리자 취소
    FAILED --> FAILED : 기본 상태
    CANCELLED --> CANCELLED : 최종 상태
    
    note right of PROCESSING
        - 결제 처리 중
        - 트랜잭션 진행
        - 원자성 보장
    end note
    
    note right of SUCCESS
        - 결제 완료
        - 좌석 소유권 확정
        - 토큰 만료 처리
    end note
    
    note right of FAILED
        - 결제 실패
        - 예약 상태 롤백
        - 좌석 해제
    end note
```

## 5. 사용자 잔액 트랜잭션 상태 다이어그램

```mermaid
stateDiagram-v2
    [*] --> CHARGE_REQUEST : 충전 요청
    [*] --> USE_REQUEST : 사용 요청
    
    CHARGE_REQUEST --> CHARGE_SUCCESS : 충전 완료
    CHARGE_REQUEST --> CHARGE_FAILED : 충전 실패
    
    USE_REQUEST --> USE_SUCCESS : 사용 완료
    USE_REQUEST --> USE_FAILED : 잔액 부족
    USE_REQUEST --> USE_FAILED : 동시성 충돌
    
    CHARGE_SUCCESS --> [*] : 이력 저장
    CHARGE_FAILED --> [*] : 실패 로그
    USE_SUCCESS --> [*] : 이력 저장
    USE_FAILED --> [*] : 실패 로그
    
    note right of CHARGE_REQUEST
        - 잔액 충전 처리
        - 락 기반 동시성 제어
    end note
    
    note right of USE_REQUEST
        - 결제 시 잔액 차감
        - 잔액 검증 필수
    end note
```

## 6. 전체 예약 프로세스 상태 다이어그램

```mermaid
stateDiagram-v2
    [*] --> TOKEN_ISSUE : 토큰 발급 요청
    
    TOKEN_ISSUE --> QUEUE_WAITING : 대기열 등록
    QUEUE_WAITING --> QUEUE_ACTIVE : 순서 도달
    
    QUEUE_ACTIVE --> SEAT_BROWSE : 좌석 조회
    SEAT_BROWSE --> SEAT_SELECT : 좌석 선택
    
    SEAT_SELECT --> RESERVATION_TEMP : 임시 예약
    RESERVATION_TEMP --> PAYMENT_PROCESS : 결제 진행
    
    PAYMENT_PROCESS --> RESERVATION_CONFIRMED : 결제 성공
    PAYMENT_PROCESS --> RESERVATION_FAILED : 결제 실패
    
    RESERVATION_TEMP --> RESERVATION_EXPIRED : 5분 만료
    
    RESERVATION_CONFIRMED --> [*] : 예약 완료
    RESERVATION_FAILED --> QUEUE_ACTIVE : 다시 시도 가능
    RESERVATION_EXPIRED --> QUEUE_ACTIVE : 다시 시도 가능
    
    QUEUE_WAITING --> TOKEN_EXPIRED : 대기 시간 초과
    QUEUE_ACTIVE --> TOKEN_EXPIRED : 활성 시간 초과
    TOKEN_EXPIRED --> [*] : 프로세스 종료
    
    note right of QUEUE_WAITING
        - 대기열에서 순서 대기
        - 실시간 상태 확인
    end note
    
    note right of RESERVATION_TEMP
        - 5분간 좌석 점유
        - 결제 완료 대기
    end note
    
    note right of PAYMENT_PROCESS
        - 원자적 결제 처리
        - 잔액 검증 및 차감
    end note
```

## 상태 전이 규칙

### 대기열 토큰
- **WAITING → ACTIVE**: 시스템에서 자동으로 순서에 따라 활성화
- **ACTIVE → EXPIRED**: 결제 완료 또는 30분 시간 만료
- **WAITING → EXPIRED**: 최대 대기 시간 초과

### 좌석 상태
- **AVAILABLE → TEMP_RESERVED**: 동시성 제어를 통한 단일 사용자만 성공
- **TEMP_RESERVED → AVAILABLE**: 5분 TTL 만료 또는 사용자 취소
- **TEMP_RESERVED → RESERVED**: 결제 완료 시에만 가능

### 예약 상태
- **임시 예약 → 확정**: 결제 완료 시에만 가능
- **임시 예약 → 만료**: 5분 후 자동 처리
- **확정 예약**: 변경 불가능한 최종 상태

### 결제 상태
- **처리 중 → 성공**: 모든 검증 통과 시
- **처리 중 → 실패**: 잔액 부족, 예약 만료, 시스템 오류 시
- **실패 → 취소**: 관리자 개입 시에만 가능