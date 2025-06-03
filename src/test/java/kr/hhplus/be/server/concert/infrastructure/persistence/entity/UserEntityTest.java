package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.User;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

/**
 * UserEntity JPA 엔티티 테스트
 * - JPA 매핑 및 변환 로직 검증
 * - 도메인 모델과 엔티티 간 변환 테스트
 */
class UserEntityTest {

    @Test
    void 엔티티_생성_테스트() {
        // given
        String uuid = "user-123";
        String name = "김철수";
        BigDecimal balance = BigDecimal.valueOf(10000);

        // when
        UserEntity entity = new UserEntity(uuid, name, balance);

        // then
        assertThat(entity.getUuid()).isEqualTo(uuid);
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getBalance()).isEqualTo(balance);
        assertThat(entity.getId()).isNull(); // 아직 저장 전이므로 ID 없음
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 도메인_모델로_변환_테스트() {
        // given
        UserEntity entity = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entity.setId(1L); // Repository에서 저장 후 ID 할당 시뮬레이션

        // when
        User user = entity.toDomain();

        // then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUuid()).isEqualTo("user-123");
        assertThat(user.getName()).isEqualTo("김철수");
        assertThat(user.getBalance()).isEqualTo(BigDecimal.valueOf(10000));
    }

    @Test
    void 도메인_모델에서_엔티티_생성_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        user.assignId(1L);

        // when
        UserEntity entity = UserEntity.fromDomain(user);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUuid()).isEqualTo("user-123");
        assertThat(entity.getName()).isEqualTo("김철수");
        assertThat(entity.getBalance()).isEqualTo(BigDecimal.valueOf(10000));
    }

    @Test
    void 도메인_모델_업데이트_테스트() {
        // given
        UserEntity entity = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entity.setId(1L);

        User updatedUser = new User("user-123", "김철수", BigDecimal.valueOf(15000));
        updatedUser.assignId(1L);
        updatedUser.chargeBalance(BigDecimal.valueOf(5000)); // 잔액 변경

        // when
        entity.updateFromDomain(updatedUser);

        // then
        assertThat(entity.getBalance()).isEqualTo(BigDecimal.valueOf(20000)); // 10000 + 5000 + 5000
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 엔티티_동등성_비교_테스트() {
        // given
        UserEntity entity1 = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entity1.setId(1L);

        UserEntity entity2 = new UserEntity("user-456", "박영희", BigDecimal.valueOf(20000));
        entity2.setId(1L);

        UserEntity entity3 = new UserEntity("user-789", "이민수", BigDecimal.valueOf(30000));
        entity3.setId(2L);

        // when & then
        assertThat(entity1).isEqualTo(entity2); // 같은 ID면 동등
        assertThat(entity1).isNotEqualTo(entity3); // 다른 ID면 다름
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    void 기본_생성자_테스트() {
        // when
        UserEntity entity = new UserEntity();

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getUuid()).isNull();
        assertThat(entity.getName()).isNull();
        assertThat(entity.getBalance()).isNull();
    }

    @Test
    void toString_테스트() {
        // given
        UserEntity entity = new UserEntity("user-123", "김철수", BigDecimal.valueOf(10000));
        entity.setId(1L);

        // when
        String result = entity.toString();

        // then
        assertThat(result).contains("UserEntity");
        assertThat(result).contains("id=1");
        assertThat(result).contains("uuid='user-123'");  // 작은따옴표 포함
        assertThat(result).contains("name='김철수'");     // 작은따옴표 포함
    }
}