package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.application.dto.PaymentResult;
import kr.hhplus.be.server.concert.application.event.EventPublisher;
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
     * @param userId 사용자 ID
     * @param concertId 콘서트 ID
     * @param seatId 좌석 ID
     * @return 예약 정보
     */
    public Reservation reserveSeat(String userId, Long concertId, Long seatId) {
        // 1. 사용자 조회
        User user = userRepository.findByUuid(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 2. 콘서트 조회
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다: " + concertId));

        // 3. 좌석 조회 및 예약 가능 여부 확인
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다: " + seatId));

        if (!seat.isAvailable()) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }

        // 4. 좌석 예약
        seat.reserve(userId);
        seatRepository.save(seat);

        // 5. 예약 정보 생성
        Reservation reservation = new Reservation(userId, concertId, seatId, seat.getSeatNumber(), seat.getPrice());
        Reservation savedReservation = reservationRepository.save(reservation);

        // 6. 예약 이벤트 발행
        eventPublisher.publishEvent(new ReservationCreatedEvent(savedReservation));

        return savedReservation;
    }

    /**
     * 결제 처리
     * @param reservationId 예약 ID
     * @param paymentMethod 결제 방법
     * @return 결제 결과
     */
    public PaymentResult processPayment(Long reservationId, String paymentMethod) {
        // 1. 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + reservationId));

        // 2. 사용자 조회
        User user = userRepository.findByUuid(reservation.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + reservation.getUserId()));

        // 3. 포인트 결제인 경우 잔액 확인
        if ("POINT".equals(paymentMethod) && !user.hasEnoughBalance(reservation.getPrice())) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        // 4. 결제 정보 생성
        Payment payment = new Payment(reservation.getUserId(), reservationId, reservation.getPrice(), paymentMethod);

        // 5. 외부 결제 시스템 호출
        String transactionId = paymentService.processPayment(payment);

        // 6. 결제 완료 처리
        payment.complete(transactionId);
        paymentRepository.save(payment);

        // 7. 포인트 결제인 경우 잔액 차감
        if ("POINT".equals(paymentMethod)) {
            user.useBalance(reservation.getPrice());
            userRepository.save(user);
        }

        // 8. 예약 확정
        reservation.confirm();
        reservationRepository.save(reservation);

        // 9. 결제 완료 이벤트 발행
        eventPublisher.publishEvent(new PaymentCompletedEvent(payment, reservation));

        return PaymentResult.success(transactionId);
    }

    /**
     * 예약 취소
     * @param reservationId 예약 ID
     * @param userId 사용자 ID
     * @return 취소 성공 여부
     */
    public boolean cancelReservation(Long reservationId, String userId) {
        // 1. 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + reservationId));

        // 2. 본인 예약인지 확인
        if (!reservation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 예약만 취소할 수 있습니다.");
        }

        // 3. 예약 취소
        reservation.cancel();
        reservationRepository.save(reservation);

        // 4. 좌석 예약 해제 (필요시)
        // seat 관련 로직은 도메인에 위임

        // 5. 취소 이벤트 발행
        eventPublisher.publishEvent(new ReservationCancelledEvent(reservation));

        return true;
    }

    // 이벤트 클래스들 (간단한 구현)
    public static class ReservationCreatedEvent {
        private final Reservation reservation;
        public ReservationCreatedEvent(Reservation reservation) { this.reservation = reservation; }
        public Reservation getReservation() { return reservation; }
    }

    public static class PaymentCompletedEvent {
        private final Payment payment;
        private final Reservation reservation;
        public PaymentCompletedEvent(Payment payment, Reservation reservation) {
            this.payment = payment;
            this.reservation = reservation;
        }
        public Payment getPayment() { return payment; }
        public Reservation getReservation() { return reservation; }
    }

    public static class ReservationCancelledEvent {
        private final Reservation reservation;
        public ReservationCancelledEvent(Reservation reservation) { this.reservation = reservation; }
        public Reservation getReservation() { return reservation; }
    }
}