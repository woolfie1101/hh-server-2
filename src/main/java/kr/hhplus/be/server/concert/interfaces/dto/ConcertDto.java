package kr.hhplus.be.server.concert.interfaces.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 콘서트 조회 응답 DTO
 */
public class ConcertDto {
    private final UUID id;
    private final String title;
    private final String artist;
    private final LocalDateTime concertDate;
    private final int totalSeats;
    private final int reservedSeats;
    private final int availableSeats;
    private final boolean soldOut;
    private final boolean bookingAvailable;

    public ConcertDto(UUID id, String title, String artist, LocalDateTime concertDate,
                     int totalSeats, int reservedSeats, int availableSeats,
                     boolean soldOut, boolean bookingAvailable) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.concertDate = concertDate;
        this.totalSeats = totalSeats;
        this.reservedSeats = reservedSeats;
        this.availableSeats = availableSeats;
        this.soldOut = soldOut;
        this.bookingAvailable = bookingAvailable;
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

    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public boolean isBookingAvailable() {
        return bookingAvailable;
    }
} 