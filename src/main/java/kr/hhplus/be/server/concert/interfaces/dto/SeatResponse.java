package kr.hhplus.be.server.concert.interfaces.dto;

import kr.hhplus.be.server.concert.application.dto.SeatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 좌석 정보 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private UUID id;
    private UUID concertId;
    private String seatNumber;
    private UUID reservedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SeatResponse from(SeatDto dto) {
        return SeatResponse.builder()
            .id(dto.getId())
            .concertId(dto.getConcertId())
            .seatNumber(dto.getSeatNumber())
            .reservedBy(dto.getReservedBy())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }

    public static List<SeatResponse> from(List<SeatDto> dtos) {
        return dtos.stream()
            .map(SeatResponse::from)
            .collect(Collectors.toList());
    }
} 