package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SpringUserJpa - Spring Data JPA 인터페이스
 * - UserEntity에 대한 데이터베이스 접근 인터페이스
 * - Spring Data JPA의 쿼리 메서드 활용
 * - 복잡한 조회는 @Query 어노테이션 사용
 */
@Repository
public interface SpringUserJpa extends JpaRepository<UserEntity, Long> {

    /**
     * UUID로 사용자 조회
     * - 비즈니스 키인 UUID로 조회하는 가장 중요한 메서드
     * - 쿼리 메서드 네이밍으로 자동 구현
     */
    Optional<UserEntity> findByUuid(String uuid);

    /**
     * 이름으로 사용자 조회 (동명이인 가능)
     * - 동일한 이름을 가진 사용자들을 모두 조회
     */
    List<UserEntity> findByName(String name);

    /**
     * 잔액 범위로 사용자 조회
     * - 특정 잔액 구간의 사용자들 조회
     * - VIP 고객 또는 잔액 부족 고객 조회용
     */
    List<UserEntity> findByBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);

    /**
     * 최소 잔액 이상 사용자 조회
     * - 특정 금액 이상의 잔액을 보유한 사용자들
     */
    List<UserEntity> findByBalanceGreaterThanEqual(BigDecimal minBalance);

    /**
     * 생성일 이후 사용자 조회
     * - 특정 날짜 이후에 가입한 사용자들
     * - 신규 가입자 조회용
     */
    List<UserEntity> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * UUID 존재 여부 확인
     * - 중복 가입 방지용
     * - exists 쿼리로 성능 최적화
     */
    boolean existsByUuid(String uuid);

    /**
     * UUID로 사용자 삭제
     * - 비즈니스 키로 삭제
     * - @Modifying 어노테이션으로 수정 쿼리임을 명시
     * - 삭제된 레코드 수 반환 (int 타입으로 제한)
     */
    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.uuid = :uuid")
    int deleteByUuid(@Param("uuid") String uuid);

    /**
     * 잔액별 사용자 수 조회 (통계용)
     * - 잔액 구간별 사용자 분포 조회
     */
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.balance >= :minBalance AND u.balance < :maxBalance")
    int countByBalanceRange(@Param("minBalance") BigDecimal minBalance,
        @Param("maxBalance") BigDecimal maxBalance);

    /**
     * 총 잔액 합계 조회 (통계용)
     * - 전체 사용자의 잔액 총합
     */
    @Query("SELECT COALESCE(SUM(u.balance), 0) FROM UserEntity u")
    BigDecimal calculateTotalBalance();

    /**
     * 평균 잔액 조회 (통계용)
     * - 전체 사용자의 평균 잔액
     */
    @Query("SELECT COALESCE(AVG(u.balance), 0) FROM UserEntity u")
    BigDecimal calculateAverageBalance();
}