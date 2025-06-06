package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.application.dto.SeatDto;
import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatUseCase {

    private final SeatRepository seatRepository;

    @Transactional
    public SeatDto createSeat(UUID concertId, String seatNumber, int price) {
        Seat seat = Seat.create(concertId, seatNumber, price);
        return new SeatDto(seatRepository.save(seat));
    }

    @Transactional(readOnly = true)
    public List<SeatDto> getAvailableSeats(UUID concertId) {
        return seatRepository.findAvailableSeats(concertId).stream()
            .map(SeatDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeatDto> getReservedSeats(UUID concertId) {
        return seatRepository.findReservedSeats(concertId).stream()
            .map(SeatDto::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public SeatDto reserveSeat(UUID seatId, UUID userId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));
        
        seat.reserve(userId);
        Seat save = seatRepository.save(seat);
        return new SeatDto(save);
    }

    @Transactional
    public SeatDto cancelReservation(UUID seatId, UUID userId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));
        
        if (!seat.isReservedBy(userId)) {
            throw new IllegalStateException("해당 좌석을 예약한 사용자가 아닙니다.");
        }
        
        seat.cancel();
        seatRepository.save(seat);
        return new SeatDto(seat);
    }

    @Transactional(readOnly = true)
    public long getAvailableSeatCount(UUID concertId) {
        return seatRepository.countByConcertIdAndStatus(concertId, ReservationStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public long getReservedSeatCount(UUID concertId) {
        return seatRepository.countByConcertIdAndStatus(concertId, ReservationStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<SeatDto> getSeats(UUID concertId) {
        return seatRepository.findByConcertId(concertId).stream()
            .map(SeatDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SeatDto getSeat(UUID seatId) {
        return new SeatDto(seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다.")));
    }
} 