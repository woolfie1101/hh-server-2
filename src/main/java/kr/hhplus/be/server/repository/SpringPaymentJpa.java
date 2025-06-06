package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.entity.PaymentEntity;
import kr.hhplus.be.server.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringPaymentJpa extends JpaRepository<PaymentEntity, UUID> {
    List<PaymentEntity> findByUserId(UUID userId);
    List<PaymentEntity> findByReservationId(UUID reservationId);
    List<PaymentEntity> findByStatus(PaymentStatus status);
    List<PaymentEntity> findByUserIdAndStatus(UUID userId, PaymentStatus status);
    List<PaymentEntity> findByReservationIdAndStatus(UUID reservationId, PaymentStatus status);
    Optional<PaymentEntity> findByTransactionId(String transactionId);
    List<PaymentEntity> findByPaymentMethod(String paymentMethod);
    List<PaymentEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<PaymentEntity> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = 'PENDING'")
    List<PaymentEntity> findPendingPayments();
    
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = 'FAILED'")
    List<PaymentEntity> findFailedPayments();
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.userId = :userId")
    BigDecimal calculateTotalAmountByUserId(UUID userId);
} 