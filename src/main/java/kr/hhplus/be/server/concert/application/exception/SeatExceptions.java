package kr.hhplus.be.server.concert.application.exception;

import java.util.UUID;

/**
 * 좌석 관련 커스텀 예외들
 */
public class SeatExceptions {

    /**
     * 좌석을 찾을 수 없는 경우
     */
    public static class SeatNotFoundException extends RuntimeException {
        public SeatNotFoundException(UUID seatId) {
            super(String.format("좌석을 찾을 수 없습니다: %s", seatId));
        }
    }

    /**
     * 이미 예약된 좌석에 대한 예외
     */
    public static class SeatAlreadyReservedException extends RuntimeException {
        public SeatAlreadyReservedException(UUID seatId) {
            super(String.format("좌석 %s는 이미 예약되었습니다.", seatId));
        }
    }

    /**
     * 좌석 작업에 대한 권한이 없는 경우
     */
    public static class UnauthorizedSeatOperationException extends RuntimeException {
        public UnauthorizedSeatOperationException(UUID seatId) {
            super(String.format("좌석 %s에 대한 작업 권한이 없습니다.", seatId));
        }
    }
} 