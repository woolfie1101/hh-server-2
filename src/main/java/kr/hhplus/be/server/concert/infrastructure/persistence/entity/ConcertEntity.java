package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.Concert;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Concert JPA 엔티티
 * - 데이터베이스 테이블 매핑 전용 객체
 * - 도메인 모델과 분리된 순수한 데이터 구조
 * - JPA 어노테이션과 DB 제약사항 포함
 */
@Entity
@Table(name = "concerts")
public class ConcertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "artist", nullable = false, length = 100)
    private String artist;

    @Column(name = "concert_date", nullable = false)
    private LocalDateTime concertDate;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "reserved_seats", nullable = false)
    private int reservedSeats;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 기본 생성자 (JPA 필수)
    protected ConcertEntity() {}

    // 비즈니스 생성자
    public ConcertEntity(String title, String artist, LocalDateTime concertDate, int totalSeats) {
        this.title = title;
        this.artist = artist;
        this.concertDate = concertDate;
        this.totalSeats = totalSeats;
        this.reservedSeats = 0; // 초기에는 예약된 좌석 없음
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 모델로 변환
     */
    public Concert toDomain() {
        Concert concert = new Concert(this.title, this.artist, this.concertDate, this.totalSeats);
        if (this.id != null) {
            concert.assignId(this.id);
        }
        return concert;
    }

    /**
     * 도메인 모델에서 엔티티 생성
     */
    public static ConcertEntity fromDomain(Concert concert) {
        ConcertEntity entity = new ConcertEntity(
            concert.getTitle(),
            concert.getArtist(),
            concert.getConcertDate(),
            concert.getTotalSeats()
        );

        if (concert.getId() != null) {
            entity.setId(concert.getId());
        }

        return entity;
    }

    /**
     * 도메인 모델의 변경사항을 엔티티에 반영
     */
    public void updateFromDomain(Concert concert) {
        this.title = concert.getTitle();
        this.artist = concert.getArtist();
        this.concertDate = concert.getConcertDate();
        this.totalSeats = concert.getTotalSeats();
        this.reservedSeats = concert.getReservedSeats();
        this.updatedAt = LocalDateTime.now();
    }

    // JPA 생명주기 콜백
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public LocalDateTime getConcertDate() {
        return concertDate;
    }

    public void setConcertDate(LocalDateTime concertDate) {
        this.concertDate = concertDate;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(int reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ConcertEntity that = (ConcertEntity) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ConcertEntity{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", artist='" + artist + '\'' +
            ", concertDate=" + concertDate +
            ", totalSeats=" + totalSeats +
            ", reservedSeats=" + reservedSeats +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}