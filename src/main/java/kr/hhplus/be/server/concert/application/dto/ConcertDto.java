package kr.hhplus.be.server.concert.application.dto;

import kr.hhplus.be.server.concert.domain.model.Concert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertDto {
    private UUID id;
    private String title;
    private String artist;
    private LocalDateTime concertDate;
    private int totalSeats;
    private int reservedSeats;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ConcertDto from(Concert concert) {
        return ConcertDto.builder()
            .id(concert.getId())
            .title(concert.getTitle())
            .artist(concert.getArtist())
            .concertDate(concert.getConcertDate())
            .totalSeats(concert.getTotalSeats())
            .reservedSeats(concert.getReservedSeats())
            .price(concert.getPrice())
            .createdAt(concert.getCreatedAt())
            .updatedAt(concert.getUpdatedAt())
            .build();
    }
} 