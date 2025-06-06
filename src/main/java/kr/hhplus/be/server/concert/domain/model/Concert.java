package kr.hhplus.be.server.concert.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 콘서트 도메인 모델
 */
public class Concert {
    private UUID id;
    private final String title;
    private final String artist;
    private final LocalDateTime concertDate;
    private final int totalSeats;
    private int reservedSeats;
    private final BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Concert(String title, String artist, LocalDateTime concertDate, int totalSeats, BigDecimal price) {
        this.title = title;
        this.artist = artist;
        this.concertDate = concertDate;
        this.totalSeats = totalSeats;
        this.price = price;
        this.reservedSeats = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reserveSeats(int count) {
        if (getAvailableSeats() < count) {
            throw new IllegalStateException("예약 가능한 좌석이 부족합니다.");
        }
        this.reservedSeats += count;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancelSeats(int count) {
        if (this.reservedSeats < count) {
            throw new IllegalStateException("취소할 좌석 수가 예약된 좌석 수보다 많습니다.");
        }
        this.reservedSeats -= count;
        this.updatedAt = LocalDateTime.now();
    }

    public int getAvailableSeats() {
        return totalSeats - reservedSeats;
    }

    public boolean isSoldOut() {
        return reservedSeats >= totalSeats;
    }

    public boolean isBookingAvailable() {
        return !isSoldOut() && concertDate.isAfter(LocalDateTime.now());
    }

    // Getters
    public UUID getId() {
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

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setReservedSeats(int reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 