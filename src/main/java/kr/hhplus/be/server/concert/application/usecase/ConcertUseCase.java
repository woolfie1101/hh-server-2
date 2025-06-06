package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.application.dto.ConcertDto;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.repository.ConcertRepository;
import kr.hhplus.be.server.concert.application.exception.ConcertExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ConcertUseCase {

    private final ConcertRepository concertRepository;

    public ConcertUseCase(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    @Transactional
    public Concert createConcert(String title, String artist, LocalDateTime concertDate, int totalSeats, BigDecimal price) {
        Concert concert = new Concert(title, artist, concertDate, totalSeats, price);
        return concertRepository.save(concert);
    }

    public ConcertDto getConcert(UUID concertId) {
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> new ConcertExceptions.ConcertNotFoundException(concertId));
        return ConcertDto.from(concert);
    }

    public List<ConcertDto> getAllConcerts() {
        return concertRepository.findAll().stream()
            .map(ConcertDto::from)
            .collect(Collectors.toList());
    }

    public List<ConcertDto> getConcertsByArtist(String artist) {
        return concertRepository.findByArtist(artist).stream()
            .map(ConcertDto::from)
            .collect(Collectors.toList());
    }

    public List<ConcertDto> getAvailableConcerts() {
        return concertRepository.findAvailableConcerts().stream()
            .map(ConcertDto::from)
            .collect(Collectors.toList());
    }

    public List<ConcertDto> getUpcomingConcerts() {
        return concertRepository.findUpcomingConcerts().stream()
            .map(ConcertDto::from)
            .collect(Collectors.toList());
    }

    public List<ConcertDto> searchConcerts(String keyword) {
        return concertRepository.searchConcerts(keyword).stream()
            .map(ConcertDto::from)
            .collect(Collectors.toList());
    }
} 