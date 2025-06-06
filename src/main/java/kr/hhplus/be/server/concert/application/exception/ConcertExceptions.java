package kr.hhplus.be.server.concert.application.exception;

import java.util.UUID;

/**
 * 콘서트 관련 커스텀 예외들
 */
public class ConcertExceptions {

    /**
     * 콘서트를 찾을 수 없는 경우
     */
    public static class ConcertNotFoundException extends RuntimeException {
        public ConcertNotFoundException(UUID concertId) {
            super(String.format("콘서트를 찾을 수 없습니다: %s", concertId));
        }
    }

    /**
     * 콘서트 예약이 불가능한 경우
     */
    public static class ConcertNotAvailableException extends RuntimeException {
        public ConcertNotAvailableException(UUID concertId) {
            super(String.format("콘서트 예약이 불가능합니다: %s", concertId));
        }
    }

    /**
     * 콘서트 좌석이 부족한 경우
     */
    public static class InsufficientSeatsException extends RuntimeException {
        public InsufficientSeatsException(UUID concertId, int requested, int available) {
            super(String.format("콘서트 %s의 예약 가능한 좌석이 부족합니다. 요청: %d, 가능: %d", concertId, requested, available));
        }
    }
} 