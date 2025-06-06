package kr.hhplus.be.server.concert.domain.exception;

public class SeatException extends RuntimeException {
    public SeatException(String message) {
        super(message);
    }

    public SeatException(String message, Throwable cause) {
        super(message, cause);
    }
} 