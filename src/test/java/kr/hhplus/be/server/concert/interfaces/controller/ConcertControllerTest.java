package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.dto.ConcertDto;
import kr.hhplus.be.server.concert.application.usecase.ConcertUseCase;
import kr.hhplus.be.server.concert.interfaces.dto.ConcertResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertControllerTest {

    @Mock
    private ConcertUseCase concertUseCase;

    @InjectMocks
    private ConcertController concertController;

    private UUID concertId;
    private ConcertDto concertDto;
    private List<ConcertDto> concertDtos;

    @BeforeEach
    void setUp() {
        concertId = UUID.randomUUID();
        concertDto = ConcertDto.builder()
            .id(concertId)
            .title("테스트 콘서트")
            .artist("테스트 아티스트")
            .concertDate(LocalDateTime.now().plusDays(7))
            .totalSeats(100)
            .reservedSeats(0)
            .price(BigDecimal.valueOf(10000))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        concertDtos = Arrays.asList(concertDto);
    }

    @Test
    void getAllConcerts_성공() {
        // given
        when(concertUseCase.getAllConcerts()).thenReturn(concertDtos);

        // when
        ResponseEntity<List<ConcertResponse>> response = concertController.getAllConcerts();

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(concertId);
    }

    @Test
    void getConcert_성공() {
        // given
        when(concertUseCase.getConcert(concertId)).thenReturn(concertDto);

        // when
        ResponseEntity<ConcertResponse> response = concertController.getConcert(concertId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isEqualTo(concertId);
    }

    @Test
    void getConcertsByArtist_성공() {
        // given
        String artist = "테스트 아티스트";
        when(concertUseCase.getConcertsByArtist(artist)).thenReturn(concertDtos);

        // when
        ResponseEntity<List<ConcertResponse>> response = concertController.getConcertsByArtist(artist);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getArtist()).isEqualTo(artist);
    }

    @Test
    void getAvailableConcerts_성공() {
        // given
        when(concertUseCase.getAvailableConcerts()).thenReturn(concertDtos);

        // when
        ResponseEntity<List<ConcertResponse>> response = concertController.getAvailableConcerts();

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(concertId);
    }

    @Test
    void getUpcomingConcerts_성공() {
        // given
        when(concertUseCase.getUpcomingConcerts()).thenReturn(concertDtos);

        // when
        ResponseEntity<List<ConcertResponse>> response = concertController.getUpcomingConcerts();

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(concertId);
    }

    @Test
    void searchConcerts_성공() {
        // given
        String keyword = "테스트";
        when(concertUseCase.searchConcerts(keyword)).thenReturn(concertDtos);

        // when
        ResponseEntity<List<ConcertResponse>> response = concertController.searchConcerts(keyword);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(concertId);
    }
} 