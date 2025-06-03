package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Payment 도메인 Repository 인터페이스
 * - 순수한 도메인 레이어의 데이터 접근 추상화
 * - 프레임워크에 독립적인 인터페이스
 * - 결제 비즈니스 로직 중심의 메서드 정의
 */
public interface PaymentRepository {

    /**
     * 결제 저장
     * @param payment 저장할 결제
     * @return 저장된 결제 (ID가 할당됨)
     */
    Payment save(Payment payment);

    /**
     * ID로 결제 조회
     * @param id 결제 ID
     * @return 조회된 결제
     */
    Optional<Payment> findById(Long id);

    /**
     * 예약 ID로 결제 조회
     * @param reservationId 예약 ID
     * @return 해당 예약의 결제
     */
    Optional<Payment> findByReservationId(Long reservationId);

    /**
     * 사용자별 결제 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 결제 목록
     */
    List<Payment> findByUserId(String userId);

    /**
     * 상태별 결제 조회
     * @param status 결제 상태
     * @return 해당 상태의 결제 목록
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * 거래 ID로 결제 조회
     * @param transactionId 외부 결제 시스템의 거래 ID
     * @return 조회된 결제
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * 결제 방법별 결제 조회
     * @param paymentMethod 결제 방법
     * @return 해당 결제 방법으로 처리된 결제 목록
     */
    List<Payment> findByPaymentMethod(String paymentMethod);

    /**
     * 날짜 범위별 결제 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 기간 내 결제 목록
     */
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 금액 범위별 결제 조회
     * @param minAmount 최소 금액
     * @param maxAmount 최대 금액
     * @return 금액 범위에 맞는 결제 목록
     */
    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * 처리 대기중인 결제 조회 (PENDING 상태)
     * @return 처리 대기중인 결제 목록
     */
    List<Payment> findPendingPayments();

    /**
     * 실패한 결제 조회 (FAILED 상태)
     * @return 실패한 결제 목록
     */
    List<Payment> findFailedPayments();

    /**
     * 사용자별 총 결제 금액 계산
     * @param userId 사용자 ID
     * @return 해당 사용자의 총 결제 금액
     */
    BigDecimal calculateTotalAmountByUserId(String userId);

    /**
     * 결제 삭제
     * @param id 삭제할 결제 ID
     */
    void deleteById(Long id);

    /**
     * 모든 결제 조회
     * @return 모든 결제 목록
     */
    List<Payment> findAll();

    /**
     * 결제 수 조회
     * @return 총 결제 수
     */
    long count();
}