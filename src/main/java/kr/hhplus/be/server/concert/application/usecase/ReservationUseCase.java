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
        // TODO: 테스트를 실패시키기 위해 일단 null 반환
        return null;
    }

    /**
     * 결제 처리
     * @param reservationId 예약 ID
     * @param paymentMethod 결제 방법
     * @return 결제 결과
     */
    public PaymentResult processPayment(Long reservationId, String paymentMethod) {
        // TODO: 테스트를 실패시키기 위해 일단 null 반환
        return null;
    }

    /**
     * 예약 취소
     * @param reservationId 예약 ID
     * @param userId 사용자 ID
     * @return 취소 성공 여부
     */
    public boolean cancelReservation(Long reservationId, String userId) {
        // TODO: 테스트를 실패시키기 위해 일단 false 반환
        return false;
    }
}