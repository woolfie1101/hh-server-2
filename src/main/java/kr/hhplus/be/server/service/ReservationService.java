package kr.hhplus.be.server.service;

import kr.hhplus.be.server.model.Reservation;
import kr.hhplus.be.server.model.ReservationStatus;
import kr.hhplus.be.server.model.Seat;
import kr.hhplus.be.server.model.SeatStatus;
import kr.hhplus.be.server.repository.ReservationRepository;
import kr.hhplus.be.server.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 예약 관련 유스케이스
 * - 예약 생성, 취소 등의 비즈니스 로직 처리
 * - 도메인 모델 간의 상호작용 조정
 */
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Reservation createReservation(UUID userId, UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }

        Reservation reservation = Reservation.create(userId, seat.getConcertId(), seatId);
        seat.reserve();
        
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("취소할 수 없는 예약 상태입니다.");
        }

        reservation.cancel();
        Seat seat = seatRepository.findById(reservation.getSeatId())
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));
        seat.cancel();

        reservationRepository.save(reservation);
        seatRepository.save(seat);
    }

    @Transactional
    public void completeReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("완료할 수 없는 예약 상태입니다.");
        }

        reservation.complete();
        reservationRepository.save(reservation);
    }

    @Transactional
    public void expireReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("만료할 수 없는 예약 상태입니다.");
        }

        reservation.expire();
        Seat seat = seatRepository.findById(reservation.getSeatId())
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));
        seat.expire();

        reservationRepository.save(reservation);
        seatRepository.save(seat);
    }

    public List<Reservation> getReservationsByUserId(UUID userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getReservationsByConcertId(UUID concertId) {
        return reservationRepository.findByConcertId(concertId);
    }

    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public List<Reservation> getExpiredReservations() {
        return reservationRepository.findExpiredReservations();
    }

    public List<Reservation> getReservationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findByCreatedAtBetween(startDate, endDate);
    }
} 