package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User 도메인 Repository 인터페이스
 * - 순수한 도메인 레이어의 데이터 접근 추상화
 * - 프레임워크에 독립적인 인터페이스
 * - 사용자 비즈니스 로직 중심의 메서드 정의
 */
public interface UserRepository {

    /**
     * 사용자 저장
     * @param user 저장할 사용자
     * @return 저장된 사용자 (ID가 할당됨)
     */
    User save(User user);

    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 조회된 사용자
     */
    Optional<User> findById(Long id);

    /**
     * UUID로 사용자 조회
     * @param uuid 사용자 UUID (비즈니스 키)
     * @return 조회된 사용자
     */
    Optional<User> findByUuid(String uuid);

    /**
     * 이름으로 사용자 조회
     * @param name 사용자 이름
     * @return 해당 이름의 사용자 목록 (동명이인 가능)
     */
    List<User> findByName(String name);

    /**
     * 잔액 범위로 사용자 조회
     * @param minBalance 최소 잔액
     * @param maxBalance 최대 잔액
     * @return 잔액 범위에 맞는 사용자 목록
     */
    List<User> findByBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);

    /**
     * 최소 잔액 이상 사용자 조회
     * @param minBalance 최소 잔액
     * @return 최소 잔액 이상의 사용자 목록
     */
    List<User> findByBalanceGreaterThanEqual(BigDecimal minBalance);

    /**
     * 특정 날짜 이후 가입한 사용자 조회
     * @param createdAt 기준 날짜
     * @return 해당 날짜 이후 가입한 사용자 목록
     */
    List<User> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * UUID 존재 여부 확인
     * @param uuid 확인할 UUID
     * @return 존재 여부
     */
    boolean existsByUuid(String uuid);

    /**
     * UUID로 사용자 삭제
     * @param uuid 삭제할 사용자 UUID
     * @return 삭제된 사용자 수
     */
    int deleteByUuid(String uuid);

    /**
     * 전체 사용자의 총 잔액 계산
     * @return 총 잔액
     */
    BigDecimal calculateTotalBalance();

    /**
     * 전체 사용자의 평균 잔액 계산
     * @return 평균 잔액
     */
    BigDecimal calculateAverageBalance();

    /**
     * ID로 사용자 삭제
     * @param id 삭제할 사용자 ID
     */
    void deleteById(Long id);

    /**
     * 모든 사용자 조회
     * @return 모든 사용자 목록
     */
    List<User> findAll();

    /**
     * 총 사용자 수 조회
     * @return 사용자 수
     */
    long count();
}