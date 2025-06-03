package kr.hhplus.be.server.concert.domain.model;

import java.time.LocalDateTime;

/**
 * 콘서트 도메인 모델 (순수 POJO)
 * - 콘서트 정보와 좌석 예약 관리
 */
public class Concert {

    private Long id;
    private String title;
    private String artist;
    private LocalDateTime concertDate;
    private int totalSeats;
    private int reservedSeats;

    // 기본 생성자 (JPA용)
    protected Concert() {}

    // 비즈니스 생성자
    public Concert(String title, String artist, LocalDateTime concertDate, int totalSeats) {
        validateTitle(title);
        validateArtist(artist);
        validateConcertDate(concertDate);
        validateTotalSeats(totalSeats);

        this.title = title;
        this.artist = artist;
        this.concertDate = concertDate;
        this.totalSeats = totalSeats;
        this.reservedSeats = 0; // 초기에는 예약된 좌석 없음
    }

    /**
     * 좌석 예약
     * @return 예약 성공 여부
     */
    public boolean reserveSeat() {
        if (!hasAvailableSeats()) {
            return false;
        }
        this.reservedSeats++;
        return true;
    }

    /**
     * 예약 취소
     */
    public void cancelReservation() {
        if (reservedSeats <= 0) {
            throw new IllegalArgumentException("취소할 예약이 없습니다.");
        }
        this.reservedSeats--;
    }

    /**
     * 예약 가능한 좌석이 있는지 확인
     */
    public boolean hasAvailableSeats() {
        return getAvailableSeats() > 0;
    }

    /**
     * 현재 예약 가능한 좌석 수
     */
    public int getAvailableSeats() {
        return totalSeats - reservedSeats;
    }

    // ID 할당 (Repository에서 사용)
    public void assignId(Long id) {
        this.id = id;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public LocalDateTime getConcertDate() {
        return concertDate;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    // 검증 메서드들
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("콘서트 제목은 필수입니다.");
        }
    }

    private void validateArtist(String artist) {
        if (artist == null || artist.trim().isEmpty()) {
            throw new IllegalArgumentException("아티스트명은 필수입니다.");
        }
    }

    private void validateConcertDate(LocalDateTime concertDate) {
        if (concertDate == null) {
            throw new IllegalArgumentException("콘서트 날짜는 필수입니다.");
        }
        if (concertDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("콘서트 날짜는 현재 시간 이후여야 합니다.");
        }
    }

    private void validateTotalSeats(int totalSeats) {
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("총 좌석 수는 1 이상이어야 합니다.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Concert concert = (Concert) obj;
        return id != null ? id.equals(concert.id) : concert.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Concert{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", artist='" + artist + '\'' +
            ", concertDate=" + concertDate +
            ", totalSeats=" + totalSeats +
            ", reservedSeats=" + reservedSeats +
            ", availableSeats=" + getAvailableSeats() +
            '}';
    }
}