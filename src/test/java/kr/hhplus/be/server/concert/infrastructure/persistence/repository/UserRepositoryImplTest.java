package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.User;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.UserEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringUserJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserRepositoryImpl 구현체 테스트
 * - 도메인 모델과 JPA 엔티티 간 변환 테스트
 * - Spring Data JPA와의 연동 테스트
 * - Mock을 사용한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private SpringUserJpa springUserJpa;

    @InjectMocks
    private UserRepositoryImpl userRepositoryImpl;

    @Test
    void 사용자_저장_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));

        UserEntity savedEntity = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        savedEntity.setId(1L);

        when(springUserJpa.save(any(UserEntity.class))).thenReturn(savedEntity);

        // when
        User result = userRepositoryImpl.save(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUuid()).isEqualTo("user-123");
        assertThat(result.getName()).isEqualTo("김철수");
        verify(springUserJpa).save(any(UserEntity.class));
    }

    @Test
    void ID로_사용자_조회_성공_테스트() {
        // given
        Long id = 1L;
        UserEntity entity = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entity.setId(id);

        when(springUserJpa.findById(id)).thenReturn(Optional.of(entity));

        // when
        Optional<User> result = userRepositoryImpl.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getUuid()).isEqualTo("user-123");
        verify(springUserJpa).findById(id);
    }

    @Test
    void ID로_사용자_조회_실패_테스트() {
        // given
        Long id = 999L;
        when(springUserJpa.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userRepositoryImpl.findById(id);

        // then
        assertThat(result).isEmpty();
        verify(springUserJpa).findById(id);
    }

    @Test
    void UUID로_사용자_조회_성공_테스트() {
        // given
        String uuid = "user-123";
        UserEntity entity = new UserEntity(uuid, "김철수", BigDecimal.valueOf(10000));
        entity.setId(1L);

        when(springUserJpa.findByUuid(uuid)).thenReturn(Optional.of(entity));

        // when
        Optional<User> result = userRepositoryImpl.findByUuid(uuid);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo(uuid);
        verify(springUserJpa).findByUuid(uuid);
    }

    @Test
    void 이름으로_사용자_조회_테스트() {
        // given
        String name = "김철수";
        UserEntity entity1 = new UserEntity("user-123", name, BigDecimal.valueOf(10000));
        UserEntity entity2 = new UserEntity("user-456", name, BigDecimal.valueOf(20000));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springUserJpa.findByName(name)).thenReturn(List.of(entity1, entity2));

        // when
        List<User> result = userRepositoryImpl.findByName(name);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName).containsOnly(name);
        assertThat(result).extracting(User::getUuid)
            .containsExactlyInAnyOrder("user-123", "user-456");
        verify(springUserJpa).findByName(name);
    }

    @Test
    void 잔액_범위로_사용자_조회_테스트() {
        // given
        BigDecimal minBalance = BigDecimal.valueOf(10000);
        BigDecimal maxBalance = BigDecimal.valueOf(30000);

        UserEntity entity1 = new UserEntity("user-123", "김철수", BigDecimal.valueOf(15000));
        UserEntity entity2 = new UserEntity("user-456", "박영희", BigDecimal.valueOf(25000));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springUserJpa.findByBalanceBetween(minBalance, maxBalance))
            .thenReturn(List.of(entity1, entity2));

        // when
        List<User> result = userRepositoryImpl.findByBalanceBetween(minBalance, maxBalance);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getBalance)
            .containsExactlyInAnyOrder(BigDecimal.valueOf(15000), BigDecimal.valueOf(25000));
        verify(springUserJpa).findByBalanceBetween(minBalance, maxBalance);
    }

    @Test
    void 최소_잔액_이상_사용자_조회_테스트() {
        // given
        BigDecimal minBalance = BigDecimal.valueOf(20000);

        UserEntity entity = new UserEntity("user-123", "김철수", BigDecimal.valueOf(25000));
        entity.setId(1L);

        when(springUserJpa.findByBalanceGreaterThanEqual(minBalance))
            .thenReturn(List.of(entity));

        // when
        List<User> result = userRepositoryImpl.findByBalanceGreaterThanEqual(minBalance);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBalance()).isGreaterThanOrEqualTo(minBalance);
        verify(springUserJpa).findByBalanceGreaterThanEqual(minBalance);
    }

    @Test
    void 생성일_이후_사용자_조회_테스트() {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        UserEntity entity = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entity.setId(1L);
        entity.setCreatedAt(LocalDateTime.now());

        when(springUserJpa.findByCreatedAtAfter(createdAt)).thenReturn(List.of(entity));

        // when
        List<User> result = userRepositoryImpl.findByCreatedAtAfter(createdAt);

        // then
        assertThat(result).hasSize(1);
        verify(springUserJpa).findByCreatedAtAfter(createdAt);
    }

    @Test
    void UUID_존재_여부_확인_테스트() {
        // given
        String uuid = "user-123";
        when(springUserJpa.existsByUuid(uuid)).thenReturn(true);
        when(springUserJpa.existsByUuid("non-existent")).thenReturn(false);

        // when
        boolean exists = userRepositoryImpl.existsByUuid(uuid);
        boolean notExists = userRepositoryImpl.existsByUuid("non-existent");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        verify(springUserJpa).existsByUuid(uuid);
        verify(springUserJpa).existsByUuid("non-existent");
    }

    @Test
    void UUID로_사용자_삭제_테스트() {
        // given
        String uuid = "user-123";
        when(springUserJpa.deleteByUuid(uuid)).thenReturn(1);

        // when
        int deletedCount = userRepositoryImpl.deleteByUuid(uuid);

        // then
        assertThat(deletedCount).isEqualTo(1);
        verify(springUserJpa).deleteByUuid(uuid);
    }

    @Test
    void 총_잔액_계산_테스트() {
        // given
        BigDecimal totalBalance = BigDecimal.valueOf(1000000);
        when(springUserJpa.calculateTotalBalance()).thenReturn(totalBalance);

        // when
        BigDecimal result = userRepositoryImpl.calculateTotalBalance();

        // then
        assertThat(result).isEqualTo(totalBalance);
        verify(springUserJpa).calculateTotalBalance();
    }

    @Test
    void 평균_잔액_계산_테스트() {
        // given
        BigDecimal avgBalance = BigDecimal.valueOf(150000);
        when(springUserJpa.calculateAverageBalance()).thenReturn(avgBalance);

        // when
        BigDecimal result = userRepositoryImpl.calculateAverageBalance();

        // then
        assertThat(result).isEqualTo(avgBalance);
        verify(springUserJpa).calculateAverageBalance();
    }

    @Test
    void ID로_사용자_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        userRepositoryImpl.deleteById(id);

        // then
        verify(springUserJpa).deleteById(id);
    }

    @Test
    void 모든_사용자_조회_테스트() {
        // given
        UserEntity entity1 = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        UserEntity entity2 = new UserEntity("user-456", "박영희", BigDecimal.valueOf(20000));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springUserJpa.findAll()).thenReturn(List.of(entity1, entity2));

        // when
        List<User> result = userRepositoryImpl.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getUuid)
            .containsExactlyInAnyOrder("user-123", "user-456");
        verify(springUserJpa).findAll();
    }

    @Test
    void 사용자_수_조회_테스트() {
        // given
        long count = 100L;
        when(springUserJpa.count()).thenReturn(count);

        // when
        long result = userRepositoryImpl.count();

        // then
        assertThat(result).isEqualTo(count);
        verify(springUserJpa).count();
    }
}