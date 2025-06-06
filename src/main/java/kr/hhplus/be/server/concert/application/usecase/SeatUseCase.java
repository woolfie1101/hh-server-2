package kr.hhplus.be.server.concert.application.usecase;

import java.util.List;

import kr.hhplus.be.server.concert.application.event.EventPublisher;
import kr.hhplus.be.server.concert.application.event.SeatEvents;
import kr.hhplus.be.server.concert.application.exception.SeatExceptions;
import kr.hhplus.be.server.concert.domain.model.*;
import kr.hhplus.be.server.concert.domain.repository.*;

/**
 * SeatUseCase - 좌석 핵심 비즈니스 로직 (클린 아키텍처)
 * - 좌석 조회, 예약, 취소 등 좌석 관련 핵심 로직
 * - 프레임워크에 독립적인 순수 비즈니스 로직
 * - 외부 의존성은 모두 인터페이스로 추상화
 */
public class SeatUseCase {

    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;
    private final EventPublisher eventPublisher;

    public SeatUseCase(
        SeatRepository seatRepository,
        ConcertRepository concertRepository,
        EventPublisher eventPublisher
    ) {
        this.seatRepository = seatRepository;
        this.concertRepository = concertRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 좌석 조회
     */
    public Seat findSeat(Long seatId) {
        return findSeatById(seatId);
    }

    /**
     * 콘서트의 좌석 목록 조회
     */
    public List<Seat> findSeatsByConcert(Long concertId) {
        // 1. 콘서트 조회
        Concert concert = findConcertById(concertId);

        // 2. 좌석 목록 조회
        return seatRepository.findByConcertId(concertId);
    }

    /**
     * 좌석 예약
     */
    public Seat reserveSeat(Long seatId, String userId) {
        // 1. 좌석 조회
        Seat seat = findSeatById(seatId);

        // 2. 좌석 상태 검증
        validateSeatAvailability(seat);

        // 3. 좌석 예약 처리
        seat.reserve(userId);
        Seat reservedSeat = seatRepository.save(seat);

        // 4. 이벤트 발행
        publishSeatReservedEvent(reservedSeat);

        return reservedSeat;
    }

    /**
     * 좌석 예약 취소
     */
    public Seat cancelSeatReservation(Long seatId, String userId) {
        // 1. 좌석 조회
        Seat seat = findSeatById(seatId);

        // 2. 권한 확인
        validateUserAuthorization(seat, userId);

        // 3. 좌석 예약 취소 처리
        seat.cancelReservation(userId);
        Seat cancelledSeat = seatRepository.save(seat);

        // 4. 이벤트 발행
        publishSeatCancelledEvent(cancelledSeat);

        return cancelledSeat;
    }

    // === Private Helper Methods ===

    private Seat findSeatById(Long seatId) {
        return seatRepository.findById(seatId)
            .orElseThrow(() -> new SeatExceptions.ResourceNotFoundException("좌석", seatId));
    }

    private Concert findConcertById(Long concertId) {
        return concertRepository.findById(concertId)
            .orElseThrow(() -> new SeatExceptions.ResourceNotFoundException("콘서트", concertId));
    }

    private void validateSeatAvailability(Seat seat) {
        if (!seat.isAvailable()) {
            throw new SeatExceptions.SeatAlreadyReservedException(seat.getId());
        }
    }

    private void validateUserAuthorization(Seat seat, String userId) {
        if (!seat.getReservedBy().equals(userId)) {
            throw new SeatExceptions.UnauthorizedAccessException("본인이 예약한 좌석만 취소할 수 있습니다");
        }
    }

    // === Event Publishing Methods ===

    private void publishSeatReservedEvent(Seat seat) {
        eventPublisher.publishEvent(new SeatEvents.SeatReserved(seat));
    }

    private void publishSeatCancelledEvent(Seat seat) {
        eventPublisher.publishEvent(new SeatEvents.SeatCancelled(seat));
    }
} 