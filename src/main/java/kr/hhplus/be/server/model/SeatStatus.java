package kr.hhplus.be.server.model;

public enum SeatStatus {
    AVAILABLE("예약 가능"),
    RESERVED("예약됨");

    private final String description;

    SeatStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 