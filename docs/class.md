# 콘서트 예약 서비스 클래스 다이어그램

## 전체 클래스 다이어그램

```mermaid
classDiagram
    %% Controller Layer
    class QueueController {
        -QueueService queueService
        +issueToken(userId: String) ResponseEntity~TokenResponse~
        +getQueueStatus(token: String) ResponseEntity~QueueStatusResponse~
    }

    class ConcertController {
        -ConcertService concertService
        +getAvailableDates(token: String) ResponseEntity~List~Date~~
        +getAvailableSeats(token: String, date: LocalDateTime) ResponseEntity~List~SeatInfo~~
    }

    class ReservationController {
        -ReservationService reservationService
        +reserveSeat(token: String, request: ReservationRequest) ResponseEntity~ReservationResponse~
    }

    class BalanceController {
        -BalanceService balanceService
        +chargeBalance(token: String, request: ChargeRequest) ResponseEntity~BalanceResponse~
        +getBalance(token: String, userId: String) ResponseEntity~BalanceResponse~
    }

    class PaymentController {
        -PaymentService paymentService
        +processPayment(token: String, reservationId: String) ResponseEntity~PaymentResponse~
    }

    %% Service Layer
    class QueueService {
        -QueueRepository queueRepository
        -RedisQueueManager redisQueueManager
        +issueToken(userId: String) TokenResponse
        +validateToken(token: String) boolean
        +getQueueStatus(token: String) QueueStatusResponse
        +activateNextUsers() void
        +expireToken(token: String) void
    }

    class ConcertService {
        -ConcertRepository concertRepository
        -ConcertScheduleRepository scheduleRepository
        -SeatRepository seatRepository
        -CacheManager cacheManager
        +getAvailableDates() List~LocalDateTime~
        +getAvailableSeats(date: LocalDateTime) List~SeatInfo~
    }

    class ReservationService {
        -ReservationRepository reservationRepository
        -SeatRepository seatRepository
        -RedisLockManager lockManager
        +reserveSeat(userId: String, seatId: Long) ReservationResponse
        +expireReservation(reservationId: String) void
        +confirmReservation(reservationId: String) void
    }

    class BalanceService {
        -UserRepository userRepository
        -BalanceHistoryRepository historyRepository
        +chargeBalance(userId: String, amount: BigDecimal) BalanceResponse
        +getBalance(userId: String) BalanceResponse
        +useBalance(userId: String, amount: BigDecimal) boolean
    }

    class PaymentService {
        -PaymentRepository paymentRepository
        -ReservationService reservationService
        -BalanceService balanceService
        -QueueService queueService
        +processPayment(reservationId: String) PaymentResponse
    }

    %% Domain Entities
    class User {
        -Long id
        -String uuid
        -String name
        -BigDecimal balance
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +chargeBalance(amount: BigDecimal) void
        +useBalance(amount: BigDecimal) boolean
        +hasEnoughBalance(amount: BigDecimal) boolean
    }

    class QueueToken {
        -Long id
        -String uuid
        -Long userId
        -TokenStatus status
        -Integer queuePosition
        -LocalDateTime issuedAt
        -LocalDateTime expiredAt
        +isActive() boolean
        +isExpired() boolean
        +activate() void
        +expire() void
    }

    class Concert {
        -Long id
        -String title
        -String description
        -LocalDateTime createdAt
        +addSchedule(schedule: ConcertSchedule) void
    }

    class ConcertSchedule {
        -Long id
        -Long concertId
        -LocalDateTime concertDate
        -LocalDateTime bookingStartAt
        -LocalDateTime bookingEndAt
        -Integer totalSeats
        -Integer availableSeats
        +isBookingAvailable() boolean
        +decreaseAvailableSeats() void
        +increaseAvailableSeats() void
    }

    class Seat {
        -Long id
        -Long scheduleId
        -Integer seatNumber
        -BigDecimal price
        -SeatStatus status
        +isAvailable() boolean
        +reserve() void
        +confirm() void
        +release() void
    }

    class Reservation {
        -Long id
        -String uuid
        -Long userId
        -Long seatId
        -ReservationStatus status
        -BigDecimal price
        -LocalDateTime reservedAt
        -LocalDateTime expiredAt
        +isExpired() boolean
        +confirm() void
        +cancel() void
        +expire() void
    }

    class Payment {
        -Long id
        -String uuid
        -Long reservationId
        -Long userId
        -BigDecimal amount
        -PaymentStatus status
        -PaymentMethod paymentMethod
        -LocalDateTime paidAt
        +isSuccess() boolean
        +markAsSuccess() void
        +markAsFailed() void
    }

    class BalanceHistory {
        -Long id
        -Long userId
        -TransactionType transactionType
        -BigDecimal amount
        -BigDecimal balanceBefore
        -BigDecimal balanceAfter
        -String description
        -LocalDateTime createdAt
    }

    %% Enums
    class TokenStatus {
        <<enumeration>>
        WAITING
        ACTIVE
        EXPIRED
    }

    class SeatStatus {
        <<enumeration>>
        AVAILABLE
        TEMP_RESERVED
        RESERVED
    }

    class ReservationStatus {
        <<enumeration>>
        TEMP_RESERVED
        CONFIRMED
        CANCELLED
        EXPIRED
    }

    class PaymentStatus {
        <<enumeration>>
        SUCCESS
        FAILED
        CANCELLED
    }

    class PaymentMethod {
        <<enumeration>>
        BALANCE
    }

    class TransactionType {
        <<enumeration>>
        CHARGE
        USE
        REFUND
    }

    %% Repository Interfaces
    class UserRepository {
        <<interface>>
        +findByUuid(uuid: String) Optional~User~
        +save(user: User) User
        +findByIdWithLock(id: Long) Optional~User~
    }

    class QueueTokenRepository {
        <<interface>>
        +findByUuid(uuid: String) Optional~QueueToken~
        +findByUserIdAndStatus(userId: Long, status: TokenStatus) Optional~QueueToken~
        +save(token: QueueToken) QueueToken
        +findActiveTokens() List~QueueToken~
    }

    class ConcertRepository {
        <<interface>>
        +findAll() List~Concert~
        +findById(id: Long) Optional~Concert~
        +save(concert: Concert) Concert
    }

    class ConcertScheduleRepository {
        <<interface>>
        +findAvailableDates() List~LocalDateTime~
        +findByDate(date: LocalDateTime) Optional~ConcertSchedule~
        +save(schedule: ConcertSchedule) ConcertSchedule
    }

    class SeatRepository {
        <<interface>>
        +findByScheduleIdAndStatus(scheduleId: Long, status: SeatStatus) List~Seat~
        +findByIdWithLock(id: Long) Optional~Seat~
        +save(seat: Seat) Seat
    }

    class ReservationRepository {
        <<interface>>
        +findByUuid(uuid: String) Optional~Reservation~
        +findExpiredReservations() List~Reservation~
        +save(reservation: Reservation) Reservation
    }

    class PaymentRepository {
        <<interface>>
        +findByReservationId(reservationId: Long) Optional~Payment~
        +save(payment: Payment) Payment
    }

    class BalanceHistoryRepository {
        <<interface>>
        +save(history: BalanceHistory) BalanceHistory
        +findByUserId(userId: Long) List~BalanceHistory~
    }

    %% Infrastructure
    class RedisQueueManager {
        -RedisTemplate redisTemplate
        +addToQueue(userId: String) Long
        +getQueuePosition(userId: String) Long
        +removeFromQueue(userId: String) void
        +getActiveUsers() Set~String~
        +activateUsers(count: Integer) void
    }

    class RedisLockManager {
        -RedisTemplate redisTemplate
        +acquireLock(key: String, timeout: Duration) boolean
        +releaseLock(key: String) void
    }

    class CacheManager {
        -RedisTemplate redisTemplate
        +get(key: String) Object
        +set(key: String, value: Object, ttl: Duration) void
        +delete(key: String) void
    }

    %% DTO Classes
    class TokenResponse {
        +String token
        +Integer queuePosition
        +String status
    }

    class QueueStatusResponse {
        +Integer queuePosition
        +String status
        +Integer estimatedWaitTime
    }

    class ReservationRequest {
        +LocalDateTime date
        +Integer seatNumber
    }

    class ReservationResponse {
        +String reservationId
        +Integer seatNumber
        +BigDecimal price
        +LocalDateTime expiredAt
    }

    class ChargeRequest {
        +String userId
        +BigDecimal amount
    }

    class BalanceResponse {
        +String userId
        +BigDecimal balance
    }

    class PaymentResponse {
        +String paymentId
        +BigDecimal amount
        +String status
        +LocalDateTime paidAt
    }

    class SeatInfo {
        +Integer seatNumber
        +BigDecimal price
        +String status
    }

    %% Relationships
    QueueController --> QueueService
    ConcertController --> ConcertService
    ReservationController --> ReservationService
    BalanceController --> BalanceService
    PaymentController --> PaymentService

    QueueService --> QueueTokenRepository
    QueueService --> RedisQueueManager
    ConcertService --> ConcertRepository
    ConcertService --> ConcertScheduleRepository
    ConcertService --> SeatRepository
    ConcertService --> CacheManager
    ReservationService --> ReservationRepository
    ReservationService --> SeatRepository
    ReservationService --> RedisLockManager
    BalanceService --> UserRepository
    BalanceService --> BalanceHistoryRepository
    PaymentService --> PaymentRepository
    PaymentService --> ReservationService
    PaymentService --> BalanceService
    PaymentService --> QueueService

    User --> BalanceHistory
    QueueToken --> TokenStatus
    Seat --> SeatStatus
    Reservation --> ReservationStatus
    Payment --> PaymentStatus
    Payment --> PaymentMethod
    BalanceHistory --> TransactionType

    Concert ||--o{ ConcertSchedule
    ConcertSchedule ||--o{ Seat
    User ||--o{ QueueToken
    User ||--o{ Reservation
    User ||--o{ Payment
    User ||--o{ BalanceHistory
    Seat ||--o| Reservation
    Reservation ||--o| Payment
```