# 콘서트 예약 서비스 API 명세서

## API 기본 정보
- **Base URL**: `http://localhost:8080/api/v1`
- **Content-Type**: `application/json`
- **Authentication**: Bearer Token (대기열 토큰)

## 1. 대기열 토큰 관리 API

### 1.1 토큰 발급
사용자의 대기열 토큰을 발급합니다.

**Endpoint**: `POST /queue/token`

**Request Body**:
```json
{
  "userId": "user-uuid-string"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "token": "queue-token-uuid",
    "queuePosition": 15,
    "status": "WAITING",
    "estimatedWaitTime": 900
  },
  "message": "토큰이 발급되었습니다."
}
```

**Status Codes**:
- `201 Created`: 토큰 발급 성공
- `400 Bad Request`: 잘못된 요청 파라미터
- `409 Conflict`: 이미 활성화된 토큰 존재

### 1.2 대기열 상태 조회
현재 대기열 상태를 조회합니다.

**Endpoint**: `GET /queue/status`

**Headers**:
```
Authorization: Bearer {queue-token}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "queuePosition": 5,
    "status": "WAITING",
    "estimatedWaitTime": 300
  }
}
```

**Status Codes**:
- `200 OK`: 조회 성공
- `401 Unauthorized`: 유효하지 않은 토큰
- `404 Not Found`: 토큰 정보 없음

## 2. 콘서트 정보 조회 API

### 2.1 예약 가능 날짜 조회
예약 가능한 콘서트 날짜 목록을 조회합니다.

**Endpoint**: `GET /concerts/dates`

**Headers**:
```
Authorization: Bearer {queue-token}
```

**Response**:
```json
{
  "success": true,
  "data": [
    {
      "date": "2024-07-15T19:00:00",
      "availableSeats": 35,
      "totalSeats": 50
    },
    {
      "date": "2024-07-16T19:00:00",
      "availableSeats": 42,
      "totalSeats": 50
    }
  ]
}
```

**Status Codes**:
- `200 OK`: 조회 성공
- `401 Unauthorized`: 토큰 검증 실패
- `403 Forbidden`: 비활성 토큰

### 2.2 좌석 정보 조회
특정 날짜의 좌석 정보를 조회합니다.

**Endpoint**: `GET /concerts/seats`

**Headers**:
```
Authorization: Bearer {queue-token}
```

**Query Parameters**:
- `date`: 콘서트 날짜 (ISO 8601 format)

**Request Example**:
```
GET /concerts/seats?date=2024-07-15T19:00:00
```

**Response**:
```json
{
  "success": true,
  "data": {
    "concertDate": "2024-07-15T19:00:00",
    "seats": [
      {
        "seatNumber": 1,
        "price": 100000,
        "status": "AVAILABLE"
      },
      {
        "seatNumber": 2,
        "price": 100000,
        "status": "RESERVED"
      },
      {
        "seatNumber": 3,
        "price": 100000,
        "status": "TEMP_RESERVED"
      }
    ]
  }
}
```

**Status Codes**:
- `200 OK`: 조회 성공
- `400 Bad Request`: 잘못된 날짜 형식
- `401 Unauthorized`: 토큰 검증 실패
- `404 Not Found`: 해당 날짜의 콘서트 없음

## 3. 좌석 예약 API

### 3.1 좌석 예약 요청
특정 좌석을 임시 예약합니다.

**Endpoint**: `POST /reservations`

**Headers**:
```
Authorization: Bearer {queue-token}
```

**Request Body**:
```json
{
  "concertDate": "2024-07-15T19:00:00",
  "seatNumber": 15
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "reservationId": "reservation-uuid",
    "seatNumber": 15,
    "price": 100000,
    "status": "TEMP_RESERVED",
    "expiredAt": "2024-06-01T14:35:00"
  },
  "message": "좌석이 임시 예약되었습니다."
}
```

**Status Codes**:
- `201 Created`: 예약 성공
- `400 Bad Request`: 잘못된 요청 데이터
- `401 Unauthorized`: 토큰 검증 실패
- `409 Conflict`: 이미 예약된 좌석
- `422 Unprocessable Entity`: 예약 불가능한 상태

## 4. 잔액 관리 API

### 4.1 잔액 충전
사용자 잔액을 충전합니다.

**Endpoint**: `POST /balance/charge`

**Headers**:
```
Authorization: Bearer {queue-token}
```

**Request Body**:
```json
{
  "userId": "user-uuid-string",
  "amount": 50000
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "userId": "user-uuid-string",
    "balance": 150000,
    "chargedAmount": 50000,
    "transactionId": "transaction-uuid"
  },
  "message": "잔액이 충전되었습니다."
}
```

**Status Codes**:
- `200 OK`: 충전 성공
- `400 Bad Request`: 잘못된 충전 금액
- `401 Unauthorized`: 토큰 검증 실패

### 4.2 잔액 조회
사용자의 현재 잔액을 조회합니다.

**Endpoint**: `GET /balance`

**Headers**:
```
Authorization: Bearer {queue-token}
```

**Query Parameters**:
- `userId`: 사용자 UUID

**Request Example**:
```
GET /balance?userId=user-uuid-string
```

**Response**:
```json
{
  "success": true,
  "data": {
    "userId": "user-uuid-string",
    "balance": 150000
  }
}
```

**Status Codes**:
- `200 OK`: 조회 성공
- `401 Unauthorized`: 토큰 검증 실패
- `404 Not Found`: 사용자 정보 없음

## 5. 결제 API

### 5.1 결제 처리
예약에 대한 결제를 처리합니다.

**Endpoint**: `POST /payments`

**Headers**:
```
Authorization: Bearer {queue-token}
```

**Request Body**:
```json
{
  "reservationId": "reservation-uuid"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "paymentId": "payment-uuid",
    "reservationId": "reservation-uuid",
    "amount": 100000,
    "status": "SUCCESS",
    "paidAt": "2024-06-01T14:32:15",
    "balance": 50000
  },
  "message": "결제가 완료되었습니다."
}
```

**Status Codes**:
- `200 OK`: 결제 성공
- `400 Bad Request`: 잘못된 예약 ID
- `401 Unauthorized`: 토큰 검증 실패
- `402 Payment Required`: 잔액 부족
- `409 Conflict`: 이미 결제 완료된 예약
- `410 Gone`: 예약 만료

## 6. 공통 응답 형식

### 성공 응답
```json
{
  "success": true,
  "data": { /* 응답 데이터 */ },
  "message": "성공 메시지"
}
```

### 오류 응답
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "오류 메시지",
    "details": "상세 오류 정보"
  },
  "timestamp": "2024-06-01T14:30:00"
}
```

## 7. 오류 코드 정의

| HTTP Status | Error Code | 설명 |
|-------------|------------|------|
| 400 | INVALID_REQUEST | 잘못된 요청 파라미터 |
| 401 | INVALID_TOKEN | 유효하지 않은 토큰 |
| 403 | TOKEN_NOT_ACTIVE | 비활성 토큰 |
| 404 | RESOURCE_NOT_FOUND | 리소스를 찾을 수 없음 |
| 409 | SEAT_ALREADY_RESERVED | 이미 예약된 좌석 |
| 409 | ACTIVE_TOKEN_EXISTS | 활성 토큰이 이미 존재 |
| 402 | INSUFFICIENT_BALANCE | 잔액 부족 |
| 410 | RESERVATION_EXPIRED | 예약 만료 |
| 422 | SEAT_NOT_AVAILABLE | 예약 불가능한 좌석 |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

## 8. 인증 및 보안

### 토큰 검증
모든 API 요청 시 `Authorization` 헤더에 대기열 토큰이 필요합니다.

```
Authorization: Bearer {queue-token-uuid}
```

### 토큰 상태별 접근 권한
- **WAITING**: 대기열 상태 조회만 가능
- **ACTIVE**: 모든 예약/결제 기능 이용 가능
- **EXPIRED**: 모든 요청 거부

## 9. Rate Limiting
- 대기열 상태 조회: 1초당 1회
- 기타 API: 1분당 10회
- 과도한 요청 시 `429 Too Many Requests` 응답

## 10. 데이터 형식
- 날짜/시간: ISO 8601 형식 (`YYYY-MM-DDTHH:mm:ss`)
- 금액: 정수형 (원 단위)
- UUID: 36자리 하이픈 포함 문자열