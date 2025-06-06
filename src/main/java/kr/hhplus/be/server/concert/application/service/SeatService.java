package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.domain.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Transactional
    public Seat createSeat(UUID id, UUID concertId, String seatNumber, int price, ReservationStatus status, UUID reservedBy,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        Seat seat = new Seat(id, concertId, seatNumber, price, status, reservedBy, createdAt, updatedAt);
        return seatRepository.save(seat);
    }

    public Optional<Seat> getSeat(UUID seatId) {
        return seatRepository.findById(seatId);
    }

    public List<Seat> getConcertSeats(UUID concertId) {
        return seatRepository.findByConcertId(concertId);
    }

    public List<Seat> getAvailableSeats(UUID concertId) {
        return seatRepository.findByConcertIdAndStatus(concertId, ReservationStatus.PENDING);
    }

    public List<Seat> getReservedSeats(UUID concertId) {
        return seatRepository.findByConcertIdAndStatus(concertId, ReservationStatus.COMPLETED);
    }

    public List<Seat> getCancelledSeats(UUID concertId) {
        return seatRepository.findByConcertIdAndStatus(concertId, ReservationStatus.CANCELLED);
    }

    public List<Seat> getExpiredSeats(UUID concertId) {
        return seatRepository.findByConcertIdAndStatus(concertId, ReservationStatus.EXPIRED);
    }

    public long countConcertSeats(UUID concertId) {
        return seatRepository.countByConcertId(concertId);
    }

    public long countAvailableSeats(UUID concertId) {
        return seatRepository.countByConcertIdAndStatus(concertId, ReservationStatus.PENDING);
    }

    public long countReservedSeats(UUID concertId) {
        return seatRepository.countByConcertIdAndStatus(concertId, ReservationStatus.COMPLETED);
    }

    public long countCancelledSeats(UUID concertId) {
        return seatRepository.countByConcertIdAndStatus(concertId, ReservationStatus.CANCELLED);
    }

    public long countExpiredSeats(UUID concertId) {
        return seatRepository.countByConcertIdAndStatus(concertId, ReservationStatus.EXPIRED);
    }

    @Transactional
    public Seat reserveSeat(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if (seat.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("예약할 수 없는 좌석 상태입니다.");
        }

        seat.reserve(seat.getId());
        return seatRepository.save(seat);
    }

    @Transactional
    public Seat completeSeat(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if (seat.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("완료할 수 없는 좌석 상태입니다.");
        }

        seat.complete();
        return seatRepository.save(seat);
    }

    @Transactional
    public Seat cancelSeat(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if (seat.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("취소할 수 없는 좌석 상태입니다.");
        }

        seat.cancel();
        return seatRepository.save(seat);
    }

    @Transactional
    public Seat expireSeat(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if (seat.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("만료할 수 없는 좌석 상태입니다.");
        }

        seat.expire();
        return seatRepository.save(seat);
    }
} 