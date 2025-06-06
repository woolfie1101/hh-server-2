package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Payment Spring Data JPA Repository
 * - JPA 전용 데이터 접근 인터페이스
 * - PaymentRepositoryImpl에서 내부적으로 사용
 */
public interface SpringPaymentJpa extends JpaRepository<PaymentEntity, Long> {

    /**
     * 예약 ID로 결제 조회
     */
    Optional<PaymentEntity> findByReservationId(Long reservationId);

    /**
     * 사용자별 결제 조회
     */
    List<PaymentEntity> findByUserId(String userId);

    /**
     * 상태별 결제 조회
     */
    List<PaymentEntity> findByStatus(PaymentStatus status);

    /**
     * 거래 ID로 결제 조회
     */
    Optional<PaymentEntity> findByTransactionId(String transactionId);

    /**
     * 결제 방법별 결제 조회
     */
    List<PaymentEntity> findByPaymentMethod(String paymentMethod);

    /**
     * 날짜 범위별 결제 조회
     */
    List<PaymentEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 금액 범위별 결제 조회
     */
    List<PaymentEntity> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * 처리 대기중인 결제 조회 (PENDING 상태)
     */
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = 'PENDING' ORDER BY p.createdAt ASC")
    List<PaymentEntity> findPendingPayments();

    /**
     * 실패한 결제 조회 (FAILED 상태)
     */
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = 'FAILED' ORDER BY p.createdAt DESC")
    List<PaymentEntity> findFailedPayments();

    /**
     * 사용자별 총 결제 금액 계산
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    BigDecimal calculateTotalAmountByUserId(@Param("userId") String userId);

    /**
     * 사용자별 결제 상태 조회
     */
    List<PaymentEntity> findByUserIdAndStatus(String userId, PaymentStatus status);

    /**
     * 예약 ID 리스트로 결제 조회
     */
    List<PaymentEntity> findByReservationIdIn(List<Long> reservationIds);

    /**
     * 만료 시간이 지난 PENDING 결제 조회 (타임아웃 처리용)
     */
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime")
    List<PaymentEntity> findExpiredPendingPayments(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 특정 기간 내 완료된 결제 조회
     */
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = 'COMPLETED' AND p.completedAt BETWEEN :startDate AND :endDate")
    List<PaymentEntity> findCompletedPaymentsBetween(@Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 날짜의 결제 방법별 통계
     */
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM PaymentEntity p " +
        "WHERE p.status = 'COMPLETED' AND DATE(p.completedAt) = DATE(:date) " +
        "GROUP BY p.paymentMethod")
    List<Object[]> findPaymentStatsByDate(@Param("date") LocalDateTime date);
}