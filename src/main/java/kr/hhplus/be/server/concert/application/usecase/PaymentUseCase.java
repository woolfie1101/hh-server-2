package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.domain.repository.ReservationRepository;
import kr.hhplus.be.server.concert.domain.repository.SeatRepository;
import kr.hhplus.be.server.model.User;
import kr.hhplus.be.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;
import kr.hhplus.be.server.concert.application.dto.PaymentDto;

@Service
@RequiredArgsConstructor
public class PaymentUseCase {

    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void processPayment(UUID userId, UUID seatId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 좌석 조회
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        // 예약 조회
        List<Reservation> reservations = reservationRepository.findBySeatId(seatId);
        if (reservations.isEmpty()) {
            throw new IllegalArgumentException("예약을 찾을 수 없습니다.");
        }
        Reservation reservation = reservations.get(0);

        // 예약 상태 확인
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("결제할 수 없는 예약 상태입니다.");
        }

        // 좌석 상태 확인
        if (seat.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("결제할 수 없는 좌석 상태입니다.");
        }

        // 결제 처리
        try {
            // 예약 완료 처리
            reservation.complete();
            reservationRepository.save(reservation);

            // 좌석 상태 업데이트
            seat.complete();
            seatRepository.save(seat);
        } catch (Exception e) {
            throw new IllegalStateException("결제 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public void cancelPayment(UUID userId, UUID seatId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 좌석 조회
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        // 예약 조회
        List<Reservation> reservations = reservationRepository.findBySeatId(seatId);
        if (reservations.isEmpty()) {
            throw new IllegalArgumentException("예약을 찾을 수 없습니다.");
        }
        Reservation reservation = reservations.get(0);

        // 예약 상태 확인
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("취소할 수 없는 예약 상태입니다.");
        }

        // 좌석 상태 확인
        if (seat.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("취소할 수 없는 좌석 상태입니다.");
        }

        // 취소 처리
        try {
            // 예약 취소 처리
            reservation.cancel();
            reservationRepository.save(reservation);

            // 좌석 상태 업데이트
            seat.cancel();
            seatRepository.save(seat);
        } catch (Exception e) {
            throw new IllegalStateException("취소 처리 중 오류가 발생했습니다.", e);
        }
    }

    public PaymentDto createPayment(UUID reservationId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public PaymentDto getPayment(UUID paymentId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<PaymentDto> getUserPayments(UUID userId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public PaymentDto completePayment(UUID paymentId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public PaymentDto cancelPayment(UUID paymentId, String reason) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }
} 