package kr.hhplus.be.server.concert.application.dto;

import kr.hhplus.be.server.concert.domain.model.Concert;

/**
 * 콘서트 예약 현황 정보 DTO
 * - 콘서트의 예약 상태를 요약한 정보
 * - 서비스 계층에서 사용하는 응답 객체
 */
public class ConcertBookingStatus {

    private final Long concertId;
    private final String title;
    private final int totalSeats;
    private final int reservedSeats;
    private final int availableSeats;
    private final boolean soldOut;
    private final boolean bookingAvailable;

    public ConcertBookingStatus(Long concertId, String title, int totalSeats, int reservedSeats,
        int availableSeats, boolean soldOut, boolean bookingAvailable) {
        this.concertId = concertId;
        this.title = title;
        this.totalSeats = totalSeats;
        this.reservedSeats = reservedSeats;
        this.availableSeats = availableSeats;
        this.soldOut = soldOut;
        this.bookingAvailable = bookingAvailable;
    }

    /**
     * Concert 도메인 모델로부터 ConcertBookingStatus 생성
     */
    public static ConcertBookingStatus from(Concert concert) {
        return new ConcertBookingStatus(
            concert.getId(),
            concert.getTitle(),
            concert.getTotalSeats(),
            concert.getReservedSeats(),
            concert.getAvailableSeats(),
            concert.isSoldOut(),
            concert.isBookingAvailable()
        );
    }

    // Getters
    public Long getConcertId() {
        return concertId;
    }

    public String getTitle() {
        return title;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public boolean isBookingAvailable() {
        return bookingAvailable;
    }

    @Override
    public String toString() {
        return "ConcertBookingStatus{" +
            "concertId=" + concertId +
            ", title='" + title + '\'' +
            ", totalSeats=" + totalSeats +
            ", reservedSeats=" + reservedSeats +
            ", availableSeats=" + availableSeats +
            ", soldOut=" + soldOut +
            ", bookingAvailable=" + bookingAvailable +
            '}';
    }
}