package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import kr.hhplus.be.server.concert.domain.repository.PaymentRepository;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.PaymentEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringPaymentJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final SpringPaymentJpa springPaymentJpa;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.from(payment);
        PaymentEntity savedEntity = springPaymentJpa.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return springPaymentJpa.findById(id)
            .map(PaymentEntity::toDomain);
    }

    @Override
    public List<Payment> findByUserId(UUID userId) {
        return springPaymentJpa.findByUserId(userId).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findBySeatId(UUID seatId) {
        return springPaymentJpa.findBySeatId(seatId).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return springPaymentJpa.findByStatus(status).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByUserIdAndStatus(UUID userId, PaymentStatus status) {
        return springPaymentJpa.findByUserIdAndStatus(userId, status).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByReservationId(UUID reservationId) {
        return springPaymentJpa.findByReservationId(reservationId).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByReservationIdAndStatus(UUID reservationId, PaymentStatus status) {
        return springPaymentJpa.findByReservationIdAndStatus(reservationId, status).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        return springPaymentJpa.findByTransactionId(transactionId)
            .map(PaymentEntity::toDomain);
    }

    @Override
    public List<Payment> findByPaymentMethod(String paymentMethod) {
        return springPaymentJpa.findByPaymentMethod(paymentMethod).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return springPaymentJpa.findByCreatedAtBetween(startDate, endDate).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return springPaymentJpa.findByAmountBetween(minAmount, maxAmount).stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findPendingPayments() {
        return springPaymentJpa.findPendingPayments().stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findFailedPayments() {
        return springPaymentJpa.findFailedPayments().stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateTotalAmountByUserId(UUID userId) {
        return springPaymentJpa.calculateTotalAmountByUserId(userId);
    }
} 