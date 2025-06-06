package kr.hhplus.be.server.concert.domain.exception;

public class ReservationException extends RuntimeException {
    public ReservationException(String message) {
        super(message);
    }

    public ReservationException(String message, Throwable cause) {
        super(message, cause);
    }
} 