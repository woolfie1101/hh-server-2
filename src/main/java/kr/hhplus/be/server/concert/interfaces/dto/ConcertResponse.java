package kr.hhplus.be.server.concert.interfaces.dto;

import kr.hhplus.be.server.concert.application.dto.ConcertDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertResponse {
    private UUID id;
    private String title;
    private String artist;
    private LocalDateTime concertDate;
    private int totalSeats;
    private int reservedSeats;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ConcertResponse from(ConcertDto dto) {
        return ConcertResponse.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .artist(dto.getArtist())
            .concertDate(dto.getConcertDate())
            .totalSeats(dto.getTotalSeats())
            .reservedSeats(dto.getReservedSeats())
            .price(dto.getPrice())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }

    public static List<ConcertResponse> from(List<ConcertDto> dtos) {
        return dtos.stream()
            .map(ConcertResponse::from)
            .collect(Collectors.toList());
    }
} 