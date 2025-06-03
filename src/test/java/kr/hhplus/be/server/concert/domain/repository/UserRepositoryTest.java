package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserRepository 인터페이스 테스트
 * - 실제 구현체가 아닌 인터페이스 스펙을 검증
 * - Mock을 사용해서 인터페이스 동작 확인
 */
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void 사용자_저장_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        User savedUser = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        savedUser.assignId(1L);

        when(userRepository.save(user)).thenReturn(savedUser);

        // when
        User result = userRepository.save(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUuid()).isEqualTo("user-123");
        verify(userRepository).save(user);
    }

    @Test
    void UUID로_사용자_조회_성공_테스트() {
        // given
        String uuid = "user-123";
        User user = new User(uuid, "김철수", BigDecimal.valueOf(10000));
        user.assignId(1L);

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        // when
        Optional<User> result = userRepository.findByUuid(uuid);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo(uuid);
        assertThat(result.get().getName()).isEqualTo("김철수");
        verify(userRepository).findByUuid(uuid);
    }

    @Test
    void UUID로_사용자_조회_실패_테스트() {
        // given
        String uuid = "nonexistent-user";

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userRepository.findByUuid(uuid);

        // then
        assertThat(result).isEmpty();
        verify(userRepository).findByUuid(uuid);
    }

    @Test
    void ID로_사용자_조회_성공_테스트() {
        // given
        Long id = 1L;
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        user.assignId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // when
        Optional<User> result = userRepository.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(userRepository).findById(id);
    }

    @Test
    void 사용자_존재_여부_확인_테스트() {
        // given
        String uuid = "user-123";

        when(userRepository.existsByUuid(uuid)).thenReturn(true);

        // when
        boolean result = userRepository.existsByUuid(uuid);

        // then
        assertThat(result).isTrue();
        verify(userRepository).existsByUuid(uuid);
    }

    @Test
    void 잔액_범위로_사용자_조회_테스트() {
        // given
        BigDecimal minBalance = BigDecimal.valueOf(5000);
        BigDecimal maxBalance = BigDecimal.valueOf(20000);

        User user1 = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        User user2 = new User("user-456", "박영희", BigDecimal.valueOf(15000));
        List<User> users = List.of(user1, user2);

        when(userRepository.findByBalanceBetween(minBalance, maxBalance)).thenReturn(users);

        // when
        List<User> result = userRepository.findByBalanceBetween(minBalance, maxBalance);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName)
            .containsExactly("김철수", "박영희");
        verify(userRepository).findByBalanceBetween(minBalance, maxBalance);
    }

    @Test
    void 사용자_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        userRepository.deleteById(id);

        // then
        verify(userRepository).deleteById(id);
    }

    @Test
    void 모든_사용자_조회_테스트() {
        // given
        User user1 = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        User user2 = new User("user-456", "박영희", BigDecimal.valueOf(15000));
        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // when
        List<User> result = userRepository.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(userRepository).findAll();
    }

    @Test
    void 사용자_수_확인_테스트() {
        // given
        long expectedCount = 5L;

        when(userRepository.count()).thenReturn(expectedCount);

        // when
        long result = userRepository.count();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(userRepository).count();
    }
}