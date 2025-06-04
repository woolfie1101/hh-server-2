package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.application.dto.PaymentResult;
import kr.hhplus.be.server.concert.application.event.EventPublisher;
import kr.hhplus.be.server.concert.application.event.ReservationEvents;
import kr.hhplus.be.server.concert.application.exception.ReservationExceptions;
import kr.hhplus.be.server.concert.application.service.PaymentService;
import kr.hhplus.be.server.concert.domain.model.*;
import kr.hhplus.be.server.concert.domain.repository.*;

/**
 * ReservationUseCase - 예약/결제 핵심 비즈니스 로직 (클린 아키텍처)
 * - 좌석 예약, 결제 프로세스, 알림 발송 등 유기적 결합
 * - 프레임워크에 독립적인 순수 비즈니스 로직
 * - 외부 의존성은 모두 인터페이스로 추상화
 */
public class ReservationUseCase {

    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final EventPublisher eventPublisher;

    public ReservationUseCase(
        UserRepository userRepository,
        ConcertRepository concertRepository,
        SeatRepository seatRepository,
        ReservationRepository reservationRepository,
        PaymentRepository paymentRepository,
        PaymentService paymentService,
        EventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.concertRepository = concertRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 좌석 예약
     */
    public Reservation reserveSeat(String userId, Long concertId, Long seatId) {
        // 1. 엔티티 조회 및 검증
        User user = findUserByUuid(userId);
        Concert concert = findConcertById(concertId);
        Seat seat = findSeatById(seatId);

        // 2. 비즈니스 규칙 검증
        validateSeatAvailability(seat);

        // 3. 좌석 예약 처리
        reserveSeatForUser(seat, userId);

        // 4. 예약 정보 생성 및 저장
        Reservation reservation = createReservation(userId, concertId, seatId, seat);

        // 5. 이벤트 발행
        publishReservationCreatedEvent(reservation);

        return reservation;
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

        // 3. 결제 처리
        Payment payment = createPayment(reservation, paymentMethod);
        String transactionId = processExternalPayment(payment);

        // 4. 결제 완료 후처리
        completePaymentProcess(payment, transactionId, user, reservation, paymentMethod);

        // 5. 이벤트 발행
        publishPaymentCompletedEvent(payment, reservation);

        return PaymentResult.success(transactionId);
    }

    /**
     * 예약 취소
     */
    public boolean cancelReservation(Long reservationId, String userId) {
        // 1. 예약 조회 및 권한 확인
        Reservation reservation = findReservationById(reservationId);
        validateUserAuthorization(reservation, userId);

        // 2. 예약 취소 처리
        cancelReservationProcess(reservation);

        // 3. 이벤트 발행
        publishReservationCancelledEvent(reservation);

        return true;
    }

    // === Private Helper Methods ===

    private User findUserByUuid(String userId) {
        return userRepository.findByUuid(userId)
            .orElseThrow(() -> new ReservationExceptions.ResourceNotFoundException("사용자", userId));
    }

    private Concert findConcertById(Long concertId) {
        return concertRepository.findById(concertId)
            .orElseThrow(() -> new ReservationExceptions.ResourceNotFoundException("콘서트", concertId));
    }

    private Seat findSeatById(Long seatId) {
        return seatRepository.findById(seatId)
            .orElseThrow(() -> new ReservationExceptions.ResourceNotFoundException("좌석", seatId));
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ReservationExceptions.ResourceNotFoundException("예약", reservationId));
    }

    private void validateSeatAvailability(Seat seat) {
        if (!seat.isAvailable()) {
            throw new ReservationExceptions.SeatAlreadyReservedException(seat.getId());
        }
    }

    private void reserveSeatForUser(Seat seat, String userId) {
        seat.reserve(userId);
        seatRepository.save(seat);
    }

    private Reservation createReservation(String userId, Long concertId, Long seatId, Seat seat) {
        Reservation reservation = new Reservation(userId, concertId, seatId, seat.getSeatNumber(), seat.getPrice());
        return reservationRepository.save(reservation);
    }

    private void validatePaymentPreconditions(User user, Reservation reservation, String paymentMethod) {
        if ("POINT".equals(paymentMethod) && !user.hasEnoughBalance(reservation.getPrice())) {
            throw new ReservationExceptions.InsufficientBalanceException(user.getUuid());
        }
    }

    private Payment createPayment(Reservation reservation, String paymentMethod) {
        return new Payment(reservation.getUserId(), reservation.getId(), reservation.getPrice(), paymentMethod);
    }

    private String processExternalPayment(Payment payment) {
        try {
            return paymentService.processPayment(payment);
        } catch (Exception e) {
            throw new ReservationExceptions.PaymentProcessingException("외부 결제 처리 중 오류가 발생했습니다.", e);
        }
    }

    private void completePaymentProcess(Payment payment, String transactionId, User user, Reservation reservation, String paymentMethod) {
        // 결제 완료 처리
        payment.complete(transactionId);
        paymentRepository.save(payment);

        // 포인트 결제인 경우 잔액 차감
        if ("POINT".equals(paymentMethod)) {
            user.useBalance(reservation.getPrice());
            userRepository.save(user);
        }

        // 예약 확정
        reservation.confirm();
        reservationRepository.save(reservation);
    }

    private void validateUserAuthorization(Reservation reservation, String userId) {
        if (!reservation.getUserId().equals(userId)) {
            throw new ReservationExceptions.UnauthorizedAccessException("본인의 예약만 취소할 수 있습니다");
        }
    }

    private void cancelReservationProcess(Reservation reservation) {
        reservation.cancel();
        reservationRepository.save(reservation);
    }

    // === Event Publishing Methods ===

    private void publishReservationCreatedEvent(Reservation reservation) {
        eventPublisher.publishEvent(new ReservationEvents.ReservationCreated(reservation));
    }

    private void publishPaymentCompletedEvent(Payment payment, Reservation reservation) {
        eventPublisher.publishEvent(new ReservationEvents.PaymentCompleted(payment, reservation));
    }

    private void publishReservationCancelledEvent(Reservation reservation) {
        eventPublisher.publishEvent(new ReservationEvents.ReservationCancelled(reservation));
    }
}