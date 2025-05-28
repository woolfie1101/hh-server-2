# 📌 도메인 모델 설계 (Java 스타일)

이 문서는 공연 티켓팅 시스템의 도메인 모델을 Java 스타일로 설계한 것입니다. 

각 도메인 클래스는 해당 시스템의 핵심 기능을 반영하며, 상태 관리와 관계를 명확히 정의합니다.

---

## 주요 도메인 클래스

```java
public class User {
    private String id;
    private String email;
    private String passwordHash;
    private int balance;
    private LocalDateTime createdAt;
    private List<Reservation> reservations;
}
```

```java
public class QueueToken {
    private String id;
    private String userId;
    private String token;
    private TokenStatus status; // WAITING, ACTIVE, EXPIRED
    private LocalDateTime issuedAt;
}
```

```java
public class ConcertDate {
    private String id;
    private LocalDate date;
    private LocalDateTime createdAt;
    private List<Seat> seats;
}
```

```java
public class Seat {
    private String id;
    private String concertDateId;
    private int seatNumber;
    private SeatStatus status; // AVAILABLE, HELD, RESERVED
}
```

```java
public class Reservation {
    private String id;
    private String userId;
    private String seatId;
    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;
    private ReservationStatus status; // PENDING, CONFIRMED, CANCELED, EXPIRED
}
```

```java
public class Payment {
    private String id;
    private String userId;
    private String reservationId;
    private int amount;
    private LocalDateTime paidAt;
    private PaymentStatus status; // SUCCESS, FAILED
}
```

---

## 상태 Enum 예시

```java
public enum TokenStatus {
    WAITING, ACTIVE, EXPIRED
}

public enum SeatStatus {
    AVAILABLE, HELD, RESERVED
}

public enum ReservationStatus {
    PENDING, CONFIRMED, CANCELED, EXPIRED
}

public enum PaymentStatus {
    SUCCESS, FAILED
}
```