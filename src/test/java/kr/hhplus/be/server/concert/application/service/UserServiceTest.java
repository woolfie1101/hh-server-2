package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.concert.domain.model.User;
import kr.hhplus.be.server.concert.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserService 테스트
 * - 레이어드 아키텍처 서비스 계층 테스트
 * - Mock을 사용한 단위 테스트
 * - 포인트 충전 및 사용자 관리 로직 테스트
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void 사용자_조회_성공_테스트() {
        // given
        Long userId = 1L;
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        user.assignId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUser(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUuid()).isEqualTo("user-123");
        assertThat(result.getName()).isEqualTo("김철수");
        verify(userRepository).findById(userId);
    }

    @Test
    void 사용자_조회_실패_테스트() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다. ID: " + userId);

        verify(userRepository).findById(userId);
    }

    @Test
    void null_사용자_ID로_조회시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> userService.getUser(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 ID는 필수입니다.");

        verify(userRepository, never()).findById(any());
    }

    @Test
    void UUID로_사용자_조회_성공_테스트() {
        // given
        String uuid = "user-123";
        User user = new User(uuid, "김철수", BigDecimal.valueOf(10000));
        user.assignId(1L);

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserByUuid(uuid);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(uuid);
        assertThat(result.getName()).isEqualTo("김철수");
        verify(userRepository).findByUuid(uuid);
    }

    @Test
    void null_UUID로_조회시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> userService.getUserByUuid(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 UUID는 필수입니다.");

        assertThatThrownBy(() -> userService.getUserByUuid(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 UUID는 필수입니다.");

        verify(userRepository, never()).findByUuid(any());
    }

    @Test
    void 포인트_충전_성공_테스트() {
        // given
        Long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(5000);
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        user.assignId(userId);

        User savedUser = new User("user-123", "김철수", BigDecimal.valueOf(15000));
        savedUser.assignId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        User result = userService.chargePoints(userId, chargeAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(15000));
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 잘못된_충전_금액으로_충전시_예외_발생_테스트() {
        // given
        Long userId = 1L;

        // when & then
        assertThatThrownBy(() -> userService.chargePoints(userId, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");

        assertThatThrownBy(() -> userService.chargePoints(userId, BigDecimal.ZERO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");

        assertThatThrownBy(() -> userService.chargePoints(userId, BigDecimal.valueOf(-1000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");

        verify(userRepository, never()).save(any());
    }

    @Test
    void UUID로_포인트_충전_테스트() {
        // given
        String uuid = "user-123";
        BigDecimal chargeAmount = BigDecimal.valueOf(5000);
        User user = new User(uuid, "김철수", BigDecimal.valueOf(10000));
        user.assignId(1L);

        User savedUser = new User(uuid, "김철수", BigDecimal.valueOf(15000));
        savedUser.assignId(1L);

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        User result = userService.chargePointsByUuid(uuid, chargeAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(15000));
        verify(userRepository).findByUuid(uuid);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 포인트_사용_성공_테스트() {
        // given
        Long userId = 1L;
        BigDecimal useAmount = BigDecimal.valueOf(3000);
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        user.assignId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        boolean result = userService.usePoints(userId, useAmount);

        // then
        assertThat(result).isTrue();
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 잔액_부족으로_포인트_사용_실패_테스트() {
        // given
        Long userId = 1L;
        BigDecimal useAmount = BigDecimal.valueOf(15000); // 잔액보다 큰 금액
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        user.assignId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        boolean result = userService.usePoints(userId, useAmount);

        // then
        assertThat(result).isFalse();
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class)); // 실패시 저장하지 않음
    }

    @Test
    void 잔액_조회_테스트() {
        // given
        Long userId = 1L;
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        user.assignId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        BigDecimal balance = userService.getBalance(userId);

        // then
        assertThat(balance).isEqualTo(BigDecimal.valueOf(10000));
        verify(userRepository).findById(userId);
    }

    @Test
    void 잔액_충분_여부_확인_테스트() {
        // given
        Long userId = 1L;
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        user.assignId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        boolean hasEnough = userService.hasEnoughBalance(userId, BigDecimal.valueOf(5000));
        boolean notEnough = userService.hasEnoughBalance(userId, BigDecimal.valueOf(15000));

        // then
        assertThat(hasEnough).isTrue();
        assertThat(notEnough).isFalse();
        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    void 이름으로_사용자_조회_테스트() {
        // given
        String name = "김철수";
        User user1 = new User("user-123", name, BigDecimal.valueOf(10000));
        User user2 = new User("user-456", name, BigDecimal.valueOf(20000));
        List<User> users = List.of(user1, user2);

        when(userRepository.findByName(name)).thenReturn(users);

        // when
        List<User> result = userService.getUsersByName(name);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName).containsOnly(name);
        verify(userRepository).findByName(name);
    }

    @Test
    void 잔액_범위로_사용자_조회_테스트() {
        // given
        BigDecimal minBalance = BigDecimal.valueOf(10000);
        BigDecimal maxBalance = BigDecimal.valueOf(30000);

        User user1 = new User("user-123", "김철수", BigDecimal.valueOf(15000));
        User user2 = new User("user-456", "박영희", BigDecimal.valueOf(25000));
        List<User> users = List.of(user1, user2);

        when(userRepository.findByBalanceBetween(minBalance, maxBalance)).thenReturn(users);

        // when
        List<User> result = userService.getUsersByBalanceRange(minBalance, maxBalance);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getBalance)
            .allMatch(balance -> balance.compareTo(minBalance) >= 0 && balance.compareTo(maxBalance) <= 0);
        verify(userRepository).findByBalanceBetween(minBalance, maxBalance);
    }

    @Test
    void 최소_잔액_이상_사용자_조회_테스트() {
        // given
        BigDecimal minBalance = BigDecimal.valueOf(20000);

        User user = new User("user-123", "김철수", BigDecimal.valueOf(25000));
        List<User> users = List.of(user);

        when(userRepository.findByBalanceGreaterThanEqual(minBalance)).thenReturn(users);

        // when
        List<User> result = userService.getUsersWithMinBalance(minBalance);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBalance()).isGreaterThanOrEqualTo(minBalance);
        verify(userRepository).findByBalanceGreaterThanEqual(minBalance);
    }

    @Test
    void 사용자_수_조회_테스트() {
        // given
        long expectedCount = 100L;
        when(userRepository.count()).thenReturn(expectedCount);

        // when
        long result = userService.getUserCount();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(userRepository).count();
    }

    @Test
    void UUID_존재_여부_확인_테스트() {
        // given
        String uuid = "user-123";
        when(userRepository.existsByUuid(uuid)).thenReturn(true);
        when(userRepository.existsByUuid("non-existent")).thenReturn(false);

        // when
        boolean exists = userService.existsByUuid(uuid);
        boolean notExists = userService.existsByUuid("non-existent");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        verify(userRepository).existsByUuid(uuid);
        verify(userRepository).existsByUuid("non-existent");
    }
}