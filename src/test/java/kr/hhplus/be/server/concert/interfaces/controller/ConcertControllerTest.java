package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.dto.ConcertBookingStatus;
import kr.hhplus.be.server.concert.application.service.ConcertService;
import kr.hhplus.be.server.concert.domain.model.Concert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertService concertService;

    @Test
    void 콘서트_조회_테스트() throws Exception {
        // given
        Long concertId = 1L;
        Concert concert = new Concert("샤이니 월드 7th", "샤이니", LocalDateTime.now().plusDays(30), 100);
        concert.assignId(concertId);

        when(concertService.getConcert(concertId)).thenReturn(concert);

        // when & then
        mockMvc.perform(get("/api/concerts/{concertId}", concertId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(concertId))
            .andExpect(jsonPath("$.title").value("샤이니 월드 7th"))
            .andExpect(jsonPath("$.artist").value("샤이니"));
    }

    @Test
    void 전체_콘서트_목록_조회_테스트() throws Exception {
        // given
        List<Concert> concerts = Arrays.asList(
            new Concert("샤이니 월드 7th", "샤이니", LocalDateTime.now().plusDays(30), 100),
            new Concert("아이유 콘서트", "아이유", LocalDateTime.now().plusDays(60), 200)
        );

        when(concertService.getAllConcerts()).thenReturn(concerts);

        // when & then
        mockMvc.perform(get("/api/concerts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].title").value("샤이니 월드 7th"))
            .andExpect(jsonPath("$[1].title").value("아이유 콘서트"));
    }

    @Test
    void 아티스트별_콘서트_조회_테스트() throws Exception {
        // given
        String artist = "샤이니";
        List<Concert> concerts = Arrays.asList(
            new Concert("샤이니 월드 7th", artist, LocalDateTime.now().plusDays(30), 100),
            new Concert("샤이니 콘서트 투어", artist, LocalDateTime.now().plusDays(60), 150)
        );

        when(concertService.getConcertsByArtist(artist)).thenReturn(concerts);

        // when & then
        mockMvc.perform(get("/api/concerts/artist/{artist}", artist))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].artist").value(artist))
            .andExpect(jsonPath("$[1].artist").value(artist));
    }

    @Test
    void 예약_가능한_콘서트_조회_테스트() throws Exception {
        // given
        List<Concert> concerts = Arrays.asList(
            new Concert("샤이니 월드 7th", "샤이니", LocalDateTime.now().plusDays(30), 100),
            new Concert("아이유 콘서트", "아이유", LocalDateTime.now().plusDays(60), 200)
        );

        when(concertService.getAvailableConcerts()).thenReturn(concerts);

        // when & then
        mockMvc.perform(get("/api/concerts/available"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].title").value("샤이니 월드 7th"))
            .andExpect(jsonPath("$[1].title").value("아이유 콘서트"));
    }

    @Test
    void 콘서트_예약_현황_조회_테스트() throws Exception {
        // given
        Long concertId = 1L;
        ConcertBookingStatus status = new ConcertBookingStatus(
            concertId,
            "샤이니 월드 7th",
            100,
            50,
            50,
            false,
            true
        );

        when(concertService.getBookingStatus(concertId)).thenReturn(status);

        // when & then
        mockMvc.perform(get("/api/concerts/{concertId}/status", concertId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.concertId").value(concertId))
            .andExpect(jsonPath("$.title").value("샤이니 월드 7th"))
            .andExpect(jsonPath("$.totalSeats").value(100))
            .andExpect(jsonPath("$.reservedSeats").value(50))
            .andExpect(jsonPath("$.availableSeats").value(50))
            .andExpect(jsonPath("$.soldOut").value(false))
            .andExpect(jsonPath("$.bookingAvailable").value(true));
    }
} 