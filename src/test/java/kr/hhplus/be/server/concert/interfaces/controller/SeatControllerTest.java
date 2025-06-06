package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.usecase.SeatUseCase;
import kr.hhplus.be.server.concert.domain.model.Seat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeatController.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatUseCase seatUseCase;

    @Test
    void 좌석_조회_테스트() throws Exception {
        // given
        Long seatId = 1L;
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);

        when(seatUseCase.findSeat(seatId)).thenReturn(seat);

        // when & then
        mockMvc.perform(get("/api/seats/{seatId}", seatId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(seatId))
            .andExpect(jsonPath("$.seatNumber").value("A-15"))
            .andExpect(jsonPath("$.price").value(150000));
    }

    @Test
    void 콘서트별_좌석_목록_조회_테스트() throws Exception {
        // given
        Long concertId = 1L;
        List<Seat> seats = Arrays.asList(
            new Seat(concertId, "A-15", BigDecimal.valueOf(150000)),
            new Seat(concertId, "A-16", BigDecimal.valueOf(150000))
        );

        when(seatUseCase.findSeatsByConcert(concertId)).thenReturn(seats);

        // when & then
        mockMvc.perform(get("/api/seats/concert/{concertId}", concertId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].seatNumber").value("A-15"))
            .andExpect(jsonPath("$[1].seatNumber").value("A-16"));
    }

    @Test
    void 좌석_예약_테스트() throws Exception {
        // given
        Long seatId = 1L;
        String userId = "user-123";
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);
        seat.reserve(userId);

        when(seatUseCase.reserveSeat(seatId, userId)).thenReturn(seat);

        // when & then
        mockMvc.perform(post("/api/seats/{seatId}/reserve", seatId)
                .param("userId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(seatId))
            .andExpect(jsonPath("$.reservedBy").value(userId));
    }

    @Test
    void 좌석_예약_취소_테스트() throws Exception {
        // given
        Long seatId = 1L;
        String userId = "user-123";
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);

        when(seatUseCase.cancelSeatReservation(seatId, userId)).thenReturn(seat);

        // when & then
        mockMvc.perform(post("/api/seats/{seatId}/cancel", seatId)
                .param("userId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(seatId))
            .andExpect(jsonPath("$.reservedBy").isEmpty());
    }
} 