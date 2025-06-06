package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.dto.SeatDto;
import kr.hhplus.be.server.concert.application.usecase.SeatUseCase;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.interfaces.dto.SeatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatControllerTest {

    @Mock
    private SeatUseCase seatUseCase;

    @InjectMocks
    private SeatController seatController;

    private UUID concertId;
    private UUID seatId;
    private UUID userId;
    private Seat seat;
    private SeatDto seatDto;
    private List<SeatDto> seatDtos;

    @BeforeEach
    void setUp() {
        concertId = UUID.randomUUID();
        seatId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        seat = mock(Seat.class);
        when(seat.getId()).thenReturn(seatId);
        when(seat.getConcertId()).thenReturn(concertId);
        when(seat.getSeatNumber()).thenReturn("A1");
        when(seat.getStatus()).thenReturn(ReservationStatus.AVAILABLE);
        when(seat.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(seat.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(seat.getReservedBy()).thenReturn(null);
        
        seatDto = new SeatDto(seat);
        seatDtos = Arrays.asList(seatDto);
    }

    @Test
    void getSeats_성공() {
        // given
        when(seatUseCase.getSeats(concertId)).thenReturn(seatDtos);

        // when
        ResponseEntity<List<SeatResponse>> response = seatController.getSeats(concertId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(seatId);
        assertThat(response.getBody().get(0).getConcertId()).isEqualTo(concertId);
    }

    @Test
    void getAvailableSeats_성공() {
        // given
        when(seatUseCase.getAvailableSeats(concertId)).thenReturn(seatDtos);

        // when
        ResponseEntity<List<SeatResponse>> response = seatController.getAvailableSeats(concertId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(seatId);
    }

    @Test
    void getReservedSeats_성공() {
        // given
        Seat reservedSeat = mock(Seat.class);
        when(reservedSeat.getId()).thenReturn(seatId);
        when(reservedSeat.getConcertId()).thenReturn(concertId);
        when(reservedSeat.getSeatNumber()).thenReturn("A1");
        when(reservedSeat.getStatus()).thenReturn(ReservationStatus.COMPLETED);
        when(reservedSeat.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(reservedSeat.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(reservedSeat.getReservedBy()).thenReturn(userId);
        
        SeatDto reservedSeatDto = new SeatDto(reservedSeat);
        when(seatUseCase.getReservedSeats(concertId)).thenReturn(Arrays.asList(reservedSeatDto));

        // when
        ResponseEntity<List<SeatResponse>> response = seatController.getReservedSeats(concertId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(seatId);
        assertThat(response.getBody().get(0).getReservedBy()).isEqualTo(userId);
    }

    @Test
    void getSeat_성공() {
        // given
        when(seatUseCase.getSeat(seatId)).thenReturn(seatDto);

        // when
        ResponseEntity<SeatResponse> response = seatController.getSeat(seatId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isEqualTo(seatId);
        assertThat(response.getBody().getConcertId()).isEqualTo(concertId);
    }

    @Test
    void reserveSeat_성공() {
        // given
        Seat reservedSeat = mock(Seat.class);
        when(reservedSeat.getId()).thenReturn(seatId);
        when(reservedSeat.getConcertId()).thenReturn(concertId);
        when(reservedSeat.getSeatNumber()).thenReturn("A1");
        when(reservedSeat.getStatus()).thenReturn(ReservationStatus.COMPLETED);
        when(reservedSeat.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(reservedSeat.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(reservedSeat.getReservedBy()).thenReturn(userId);
        
        SeatDto reservedSeatDto = new SeatDto(reservedSeat);
        when(seatUseCase.reserveSeat(seatId, userId)).thenReturn(reservedSeatDto);

        // when
        ResponseEntity<SeatResponse> response = seatController.reserveSeat(concertId, seatId, userId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isEqualTo(seatId);
        assertThat(response.getBody().getReservedBy()).isEqualTo(userId);
    }

    @Test
    void cancelReservation_성공() {
        // given
        Seat cancelledSeat = mock(Seat.class);
        when(cancelledSeat.getId()).thenReturn(seatId);
        when(cancelledSeat.getConcertId()).thenReturn(concertId);
        when(cancelledSeat.getSeatNumber()).thenReturn("A1");
        when(cancelledSeat.getStatus()).thenReturn(ReservationStatus.AVAILABLE);
        when(cancelledSeat.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(cancelledSeat.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(cancelledSeat.getReservedBy()).thenReturn(null);
        
        SeatDto cancelledSeatDto = new SeatDto(cancelledSeat);
        when(seatUseCase.cancelReservation(seatId, userId)).thenReturn(cancelledSeatDto);

        // when
        ResponseEntity<SeatResponse> response = seatController.cancelReservation(concertId, seatId, userId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isEqualTo(seatId);
        assertThat(response.getBody().getReservedBy()).isNull();
    }
} 