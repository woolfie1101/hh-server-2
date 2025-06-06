package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.application.dto.PaymentResult;
import kr.hhplus.be.server.concert.application.event.EventPublisher;
import kr.hhplus.be.server.concert.application.event.PaymentEvents;
import kr.hhplus.be.server.concert.application.exception.PaymentExceptions;
import kr.hhplus.be.server.concert.application.service.PaymentService;
import kr.hhplus.be.server.concert.domain.model.*;
import kr.hhplus.be.server.concert.domain.repository.*;

/**
 * PaymentUseCase - 결제 핵심 비즈니스 로직 (클린 아키텍처)
 * - 결제 처리, 취소, 환불 등 결제 관련 핵심 로직
 * - 프레임워크에 독립적인 순수 비즈니스 로직
 * - 외부 의존성은 모두 인터페이스로 추상화
 */
public class PaymentUseCase {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;
    private final EventPublisher eventPublisher;

    public PaymentUseCase(
        UserRepository userRepository,
        PaymentRepository paymentRepository,
        ReservationRepository reservationRepository,
        PaymentService paymentService,
        EventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.paymentService = paymentService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 결제 처리
     */
    public PaymentResult processPayment(Long reservationId, String paymentMethod) {
        // 1. 예약 및 사용자 조회
        Reservation reservation = findReservationById(reservationId);
        User user = findUserByUuid(reservation.getUserId());

        // 2. 결제 전 검증
        validatePaymentPreconditions(user, reservation, paymentMethod);

        // 3. 결제 생성 및 처리
        Payment payment = createPayment(reservation, paymentMethod);
        String transactionId = processExternalPayment(payment);

        // 4. 결제 완료 후처리
        completePaymentProcess(payment, transactionId, user, reservation);

        // 5. 이벤트 발행
        publishPaymentCompletedEvent(payment, reservation);

        return PaymentResult.success(transactionId);
    }

    /**
     * 결제 취소
     */
    public boolean cancelPayment(Long paymentId, String userId) {
        // 1. 결제 조회 및 권한 확인
        Payment payment = findPaymentById(paymentId);
        validateUserAuthorization(payment, userId);

        // 2. 결제 취소 처리
        cancelPaymentProcess(payment);

        // 3. 이벤트 발행
        publishPaymentCancelledEvent(payment);

        return true;
    }

    /**
     * 결제 환불
     */
    public boolean refundPayment(Long paymentId, String userId, String refundReason) {
        // 1. 결제 조회 및 권한 확인
        Payment payment = findPaymentById(paymentId);
        validateUserAuthorization(payment, userId);

        // 2. 환불 가능 여부 확인
        validateRefundPreconditions(payment);

        // 3. 환불 처리
        refundPaymentProcess(payment, refundReason);

        // 4. 이벤트 발행
        publishPaymentRefundedEvent(payment);

        return true;
    }

    // === Private Helper Methods ===

    private User findUserByUuid(String userId) {
        return userRepository.findByUuid(userId)
            .orElseThrow(() -> new PaymentExceptions.ResourceNotFoundException("사용자", userId));
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new PaymentExceptions.ResourceNotFoundException("예약", reservationId));
    }

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentExceptions.ResourceNotFoundException("결제", paymentId));
    }

    private void validatePaymentPreconditions(User user, Reservation reservation, String paymentMethod) {
        // 포인트 결제인 경우 잔액 확인
        if ("POINT".equals(paymentMethod) && !user.hasEnoughBalance(reservation.getPrice())) {
            throw new PaymentExceptions.InsufficientBalanceException(user.getUuid());
        }

        // 이미 결제된 예약인지 확인
        if (reservation.isPaid()) {
            throw new PaymentExceptions.AlreadyPaidException(reservation.getId());
        }
    }

    private Payment createPayment(Reservation reservation, String paymentMethod) {
        return new Payment(reservation.getUserId(), reservation.getId(), reservation.getPrice(), paymentMethod);
    }

    private String processExternalPayment(Payment payment) {
        try {
            return paymentService.processPayment(payment);
        } catch (Exception e) {
            throw new PaymentExceptions.PaymentProcessingException("외부 결제 처리 중 오류가 발생했습니다.", e);
        }
    }

    private void completePaymentProcess(Payment payment, String transactionId, User user, Reservation reservation) {
        // 결제 완료 처리
        payment.complete(transactionId);
        paymentRepository.save(payment);

        // 포인트 결제인 경우 잔액 차감
        if ("POINT".equals(payment.getPaymentMethod())) {
            user.useBalance(reservation.getPrice());
            userRepository.save(user);
        }

        // 예약 확정
        reservation.confirm();
        reservationRepository.save(reservation);
    }

    private void validateUserAuthorization(Payment payment, String userId) {
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentExceptions.UnauthorizedAccessException("본인의 결제만 취소/환불할 수 있습니다");
        }
    }

    private void cancelPaymentProcess(Payment payment) {
        if (!payment.canBeProcessed()) {
            throw new PaymentExceptions.InvalidPaymentStatusException("취소할 수 없는 결제 상태입니다");
        }

        payment.cancel();
        paymentRepository.save(payment);
    }

    private void validateRefundPreconditions(Payment payment) {
        if (!payment.canBeRefunded()) {
            throw new PaymentExceptions.InvalidPaymentStatusException("환불할 수 없는 결제 상태입니다");
        }
    }

    private void refundPaymentProcess(Payment payment, String refundReason) {
        payment.refund(refundReason);
        paymentRepository.save(payment);
    }

    // === Event Publishing Methods ===

    private void publishPaymentCompletedEvent(Payment payment, Reservation reservation) {
        eventPublisher.publishEvent(new PaymentEvents.PaymentCompleted(payment, reservation));
    }

    private void publishPaymentCancelledEvent(Payment payment) {
        eventPublisher.publishEvent(new PaymentEvents.PaymentCancelled(payment));
    }

    private void publishPaymentRefundedEvent(Payment payment) {
        eventPublisher.publishEvent(new PaymentEvents.PaymentRefunded(payment));
    }
} 