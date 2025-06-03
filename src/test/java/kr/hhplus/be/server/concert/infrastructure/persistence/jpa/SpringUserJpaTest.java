package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

/**
 * SpringUserJpa Spring Data JPA 인터페이스 테스트
 * - @DataJpaTest를 사용한 슬라이스 테스트
 * - 실제 H2 인메모리 DB와 연동 테스트
 * - JPA 쿼리 메서드 동작 검증
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
class SpringUserJpaTest {

    @Autowired
    private SpringUserJpa springUserJpa;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void UUID로_사용자_조회_테스트() {
        // given
        UserEntity user = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entityManager.persistAndFlush(user);

        // when
        Optional<UserEntity> result = springUserJpa.findByUuid("user-123");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo("user-123");
        assertThat(result.get().getName()).isEqualTo("김철수");
    }

    @Test
    void UUID로_사용자_조회_실패_테스트() {
        // when
        Optional<UserEntity> result = springUserJpa.findByUuid("non-existent");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 이름으로_사용자_조회_테스트() {
        // given
        UserEntity user1 = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        UserEntity user2 = new UserEntity("user-456", "김철수", BigDecimal.valueOf(20000));
        UserEntity user3 = new UserEntity("user-789", "박영희", BigDecimal.valueOf(30000));

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // when
        List<UserEntity> result = springUserJpa.findByName("김철수");

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserEntity::getName)
            .containsOnly("김철수");
    }

    @Test
    void 잔액_범위로_사용자_조회_테스트() {
        // given
        UserEntity user1 = new UserEntity("user-123", "김철수", BigDecimal.valueOf(5000));   // 범위 밖
        UserEntity user2 = new UserEntity("user-456", "박영희", BigDecimal.valueOf(15000));  // 범위 안
        UserEntity user3 = new UserEntity("user-789", "이민수", BigDecimal.valueOf(25000));  // 범위 안
        UserEntity user4 = new UserEntity("user-999", "최민정", BigDecimal.valueOf(35000));  // 범위 밖

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.persist(user4);
        entityManager.flush();

        // when
        List<UserEntity> result = springUserJpa.findByBalanceBetween(
            BigDecimal.valueOf(10000),
            BigDecimal.valueOf(30000)
        );

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserEntity::getBalance)
            .containsExactlyInAnyOrder(
                BigDecimal.valueOf(15000),
                BigDecimal.valueOf(25000)
            );
    }

    @Test
    void 잔액_이상으로_사용자_조회_테스트() {
        // given
        UserEntity user1 = new UserEntity("user-123", "김철수", BigDecimal.valueOf(5000));   // 범위 밖
        UserEntity user2 = new UserEntity("user-456", "박영희", BigDecimal.valueOf(15000));  // 범위 안
        UserEntity user3 = new UserEntity("user-789", "이민수", BigDecimal.valueOf(25000));  // 범위 안

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // when
        List<UserEntity> result = springUserJpa.findByBalanceGreaterThanEqual(BigDecimal.valueOf(15000));

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserEntity::getName)
            .containsExactlyInAnyOrder("박영희", "이민수");
    }

    @Test
    void 생성일_이후로_사용자_조회_테스트() {
        // given
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1);

        UserEntity oldUser = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        oldUser.setCreatedAt(LocalDateTime.now().minusHours(2)); // 1시간 전보다 더 오래된 사용자

        UserEntity newUser = new UserEntity("user-456", "박영희", BigDecimal.valueOf(20000));
        // newUser는 현재 시간으로 생성됨 (1시간 전보다 최근)

        entityManager.persist(oldUser);
        entityManager.persist(newUser);
        entityManager.flush();

        // when
        List<UserEntity> result = springUserJpa.findByCreatedAtAfter(cutoffTime);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("박영희");
    }

    @Test
    void UUID_존재_여부_확인_테스트() {
        // given
        UserEntity user = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entityManager.persistAndFlush(user);

        // when
        boolean exists = springUserJpa.existsByUuid("user-123");
        boolean notExists = springUserJpa.existsByUuid("non-existent");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void 총_사용자_수_조회_테스트() {
        // given
        UserEntity user1 = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        UserEntity user2 = new UserEntity("user-456", "박영희", BigDecimal.valueOf(20000));
        UserEntity user3 = new UserEntity("user-789", "이민수", BigDecimal.valueOf(30000));

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // when
        long count = springUserJpa.count();

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void 사용자_삭제_테스트() {
        // given
        UserEntity user = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        UserEntity savedUser = entityManager.persistAndFlush(user);
        Long userId = savedUser.getId();

        // when
        springUserJpa.deleteById(userId);
        entityManager.flush();

        // then
        Optional<UserEntity> result = springUserJpa.findById(userId);
        assertThat(result).isEmpty();
    }

    @Test
    void UUID로_사용자_삭제_테스트() {
        // given
        UserEntity user = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entityManager.persistAndFlush(user);

        // when
        int deletedCount = springUserJpa.deleteByUuid("user-123");

        // then
        assertThat(deletedCount).isEqualTo(1);

        Optional<UserEntity> result = springUserJpa.findByUuid("user-123");
        assertThat(result).isEmpty();
    }

    @Test
    void 모든_사용자_조회_테스트() {
        // given
        UserEntity user1 = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        UserEntity user2 = new UserEntity("user-456", "박영희", BigDecimal.valueOf(20000));

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // when
        List<UserEntity> result = springUserJpa.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserEntity::getUuid)
            .containsExactlyInAnyOrder("user-123", "user-456");
    }
}