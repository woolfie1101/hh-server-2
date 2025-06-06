package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.model.Concert;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 콘서트 JPA 엔티티
 */
@Entity
@Table(name = "concerts")
@Getter
@NoArgsConstructor
public class ConcertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public static ConcertEntity from(Concert concert) {
        ConcertEntity entity = new ConcertEntity();
        entity.id = concert.getId();
        entity.title = concert.getTitle();
        entity.artist = concert.getArtist();
        entity.concertDate = concert.getConcertDate();
        entity.totalSeats = concert.getTotalSeats();
        entity.reservedSeats = concert.getReservedSeats();
        entity.price = concert.getPrice();
        entity.createdAt = concert.getCreatedAt();
        entity.updatedAt = concert.getUpdatedAt();
        return entity;
    }

    public Concert toDomain() {
        Concert concert = new Concert(
            this.title,
            this.artist,
            this.concertDate,
            this.totalSeats,
            this.price
        );
        concert.setId(this.id);
        concert.setReservedSeats(this.reservedSeats);
        concert.setCreatedAt(this.createdAt);
        concert.setUpdatedAt(this.updatedAt);
        return concert;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public int getAvailableSeats() {
        return totalSeats - reservedSeats;
    }

    public boolean isSoldOut() {
        return reservedSeats >= totalSeats;
    }

    public boolean isBookingAvailable() {
        return !isSoldOut() && concertDate.isAfter(LocalDateTime.now());
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
            ", price=" + price +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}