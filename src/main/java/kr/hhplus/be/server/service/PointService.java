package kr.hhplus.be.server.service;

import kr.hhplus.be.server.repository.SpringUserJpa;
import kr.hhplus.be.server.entity.UserEntity;
import kr.hhplus.be.server.dto.PointChargeResponse;
import kr.hhplus.be.server.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 포인트 충전 서비스
 */
@Service
@Transactional
public class PointService {

    private final SpringUserJpa userJpa;

    public PointService(SpringUserJpa userJpa) {
        this.userJpa = userJpa;
    }

    /**
     * 포인트 충전
     * @param userId 사용자 ID
     * @param amount 충전할 금액
     * @return 충전 결과
     */
    public PointChargeResponse chargePoints(UUID userId, BigDecimal amount) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        User user = userJpa.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.charge(amount);
        User savedUser = userJpa.save(user);

        return new PointChargeResponse(
            savedUser.getId(),
            amount,
            savedUser.getBalance(),
            savedUser.getUpdatedAt()
        );
    }

    /**
     * 포인트 잔액 조회
     * @param userId 사용자 ID
     * @return 현재 잔액
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        return userJpa.findById(userId)
            .map(User::getBalance)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
    }
} 