package kr.hhplus.be.server.concert.application.exception;

/**
 * 좌석 관련 예외 클래스들
 */
public class SeatExceptions {

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String resourceType, Object id) {
            super(String.format("%s(ID: %s)를 찾을 수 없습니다.", resourceType, id));
        }
    }

    public static class SeatAlreadyReservedException extends RuntimeException {
        public SeatAlreadyReservedException(Long seatId) {
            super(String.format("좌석(ID: %d)은 이미 예약되었습니다.", seatId));
        }
    }

    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }
} 