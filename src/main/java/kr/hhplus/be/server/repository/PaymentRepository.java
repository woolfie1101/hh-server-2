package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.model.Payment;
import kr.hhplus.be.server.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 결제 레포지토리 인터페이스
 */
public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    List<Payment> findByUserId(UUID userId);
    List<Payment> findBySeatId(UUID seatId);
    List<Payment> findByReservationId(UUID reservationId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByUserIdAndStatus(UUID userId, PaymentStatus status);
    List<Payment> findByReservationIdAndStatus(UUID reservationId, PaymentStatus status);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByPaymentMethod(String paymentMethod);
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    List<Payment> findPendingPayments();
    List<Payment> findFailedPayments();
    BigDecimal calculateTotalAmountByUserId(UUID userId);
}