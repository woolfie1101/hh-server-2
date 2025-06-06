package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import kr.hhplus.be.server.concert.domain.repository.PaymentRepository;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.PaymentEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringPaymentJpa;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Payment Repository 구현체
 * - 도메인 Repository 인터페이스 구현
 * - Spring Data JPA를 내부적으로 사용
 * - 도메인 ↔ JPA 엔티티 매핑 처리
 */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final SpringPaymentJpa springPaymentJpa;

    public PaymentRepositoryImpl(SpringPaymentJpa springPaymentJpa) {
        this.springPaymentJpa = springPaymentJpa;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.fromDomain(payment);
        PaymentEntity savedEntity = springPaymentJpa.save(entity);

        // 저장된 ID를 도메인 객체에 할당
        if (savedEntity.getId() != null) {
            payment.assignId(savedEntity.getId());
        }

        return payment;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return springPaymentJpa.findById(id)
            .map(PaymentEntity::toDomain);
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return springPaymentJpa.findByReservationId(reservationId)
            .map(PaymentEntity::toDomain);
    }

    @Override
    public List<Payment> findByUserId(String userId) {
        return springPaymentJpa.findByUserId(userId)
            .stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return springPaymentJpa.findByStatus(status)
            .stream()
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
        return springPaymentJpa.findByPaymentMethod(paymentMethod)
            .stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return springPaymentJpa.findByCreatedAtBetween(startDate, endDate)
            .stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return springPaymentJpa.findByAmountBetween(minAmount, maxAmount)
            .stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findPendingPayments() {
        return springPaymentJpa.findPendingPayments()
            .stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findFailedPayments() {
        return springPaymentJpa.findFailedPayments()
            .stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateTotalAmountByUserId(String userId) {
        BigDecimal totalAmount = springPaymentJpa.calculateTotalAmountByUserId(userId);
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    @Override
    public void deleteById(Long id) {
        springPaymentJpa.deleteById(id);
    }

    @Override
    public List<Payment> findAll() {
        return springPaymentJpa.findAll()
            .stream()
            .map(PaymentEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return springPaymentJpa.count();
    }
}