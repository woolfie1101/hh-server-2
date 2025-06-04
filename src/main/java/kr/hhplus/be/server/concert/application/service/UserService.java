package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.concert.domain.model.User;
import kr.hhplus.be.server.concert.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * UserService - 사용자 포인트 충전 서비스 (레이어드 아키텍처)
 * - 사용자 관련 비즈니스 로직 처리
 * - 포인트 충전 및 사용자 관리 기능 중심
 * - 도메인 모델의 비즈니스 로직 활용
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 사용자 조회 (ID)
     * @param userId 사용자 ID
     * @return 조회된 사용자
     * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
     */
    public User getUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    /**
     * 사용자 조회 (UUID)
     * @param uuid 사용자 UUID
     * @return 조회된 사용자
     * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
     */
    public User getUserByUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
        }

        return userRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. UUID: " + uuid));
    }

    /**
     * 포인트 충전
     * @param userId 사용자 ID
     * @param amount 충전할 금액
     * @return 충전 후 사용자 정보
     */
    @Transactional
    public User chargePoints(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        User user = getUser(userId);
        user.chargeBalance(amount);

        return userRepository.save(user);
    }

    /**
     * 포인트 충전 (UUID 기반)
     * @param uuid 사용자 UUID
     * @param amount 충전할 금액
     * @return 충전 후 사용자 정보
     */
    @Transactional
    public User chargePointsByUuid(String uuid, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        User user = getUserByUuid(uuid);
        user.chargeBalance(amount);

        return userRepository.save(user);
    }

    /**
     * 포인트 사용
     * @param userId 사용자 ID
     * @param amount 사용할 금액
     * @return 사용 성공 여부
     */
    @Transactional
    public boolean usePoints(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }

        User user = getUser(userId);
        boolean success = user.useBalance(amount);

        if (success) {
            userRepository.save(user);
        }

        return success;
    }

    /**
     * 잔액 조회
     * @param userId 사용자 ID
     * @return 현재 잔액
     */
    public BigDecimal getBalance(Long userId) {
        User user = getUser(userId);
        return user.getBalance();
    }

    /**
     * 잔액 충분 여부 확인
     * @param userId 사용자 ID
     * @param amount 필요한 금액
     * @return 잔액 충분 여부
     */
    public boolean hasEnoughBalance(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        User user = getUser(userId);
        return user.hasEnoughBalance(amount);
    }

    /**
     * 이름으로 사용자 조회
     * @param name 사용자 이름
     * @return 해당 이름의 사용자 목록
     */
    public List<User> getUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }

        return userRepository.findByName(name);
    }

    /**
     * 잔액 범위로 사용자 조회
     * @param minBalance 최소 잔액
     * @param maxBalance 최대 잔액
     * @return 조건에 맞는 사용자 목록
     */
    public List<User> getUsersByBalanceRange(BigDecimal minBalance, BigDecimal maxBalance) {
        if (minBalance == null || maxBalance == null) {
            throw new IllegalArgumentException("최소 잔액과 최대 잔액은 필수입니다.");
        }
        if (minBalance.compareTo(maxBalance) > 0) {
            throw new IllegalArgumentException("최소 잔액은 최대 잔액보다 작거나 같아야 합니다.");
        }

        return userRepository.findByBalanceBetween(minBalance, maxBalance);
    }

    /**
     * 최소 잔액 이상 사용자 조회
     * @param minBalance 최소 잔액
     * @return 조건에 맞는 사용자 목록
     */
    public List<User> getUsersWithMinBalance(BigDecimal minBalance) {
        if (minBalance == null) {
            throw new IllegalArgumentException("최소 잔액은 필수입니다.");
        }

        return userRepository.findByBalanceGreaterThanEqual(minBalance);
    }

    /**
     * 사용자 수 조회
     * @return 총 사용자 수
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * UUID 존재 여부 확인
     * @param uuid 확인할 UUID
     * @return 존재 여부
     */
    public boolean existsByUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return false;
        }

        return userRepository.existsByUuid(uuid);
    }
}