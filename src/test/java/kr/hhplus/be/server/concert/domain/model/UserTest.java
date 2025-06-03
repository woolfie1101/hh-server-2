package kr.hhplus.be.server.concert.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void 사용자_생성_테스트() {
        // given
        String uuid = "user-123";
        String name = "김철수";
        BigDecimal initialBalance = BigDecimal.valueOf(10000);

        // when
        User user = new User(uuid, name, initialBalance);

        // then
        assertThat(user.getUuid()).isEqualTo(uuid);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getBalance()).isEqualTo(initialBalance);
    }

    @Test
    void 잔액_충전_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        BigDecimal chargeAmount = BigDecimal.valueOf(5000);

        // when
        user.chargeBalance(chargeAmount);

        // then
        assertThat(user.getBalance()).isEqualTo(BigDecimal.valueOf(15000));
    }

    @Test
    void 잔액_사용_성공_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));
        BigDecimal useAmount = BigDecimal.valueOf(3000);

        // when
        boolean result = user.useBalance(useAmount);

        // then
        assertThat(result).isTrue();
        assertThat(user.getBalance()).isEqualTo(BigDecimal.valueOf(7000));
    }

    @Test
    void 잔액_부족시_사용_실패_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(1000));
        BigDecimal useAmount = BigDecimal.valueOf(5000);

        // when
        boolean result = user.useBalance(useAmount);

        // then
        assertThat(result).isFalse();
        assertThat(user.getBalance()).isEqualTo(BigDecimal.valueOf(1000)); // 잔액 변경 안됨
    }

    @Test
    void 충분한_잔액_확인_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));

        // when & then
        assertThat(user.hasEnoughBalance(BigDecimal.valueOf(5000))).isTrue();
        assertThat(user.hasEnoughBalance(BigDecimal.valueOf(10000))).isTrue();
        assertThat(user.hasEnoughBalance(BigDecimal.valueOf(15000))).isFalse();
    }

    @Test
    void 음수_충전_시_예외_발생_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));

        // when & then
        assertThatThrownBy(() -> user.chargeBalance(BigDecimal.valueOf(-1000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 양수여야 합니다.");
    }

    @Test
    void null_값으로_사용_시_예외_발생_테스트() {
        // given
        User user = new User("user-123", "김철수", BigDecimal.valueOf(10000));

        // when & then
        assertThatThrownBy(() -> user.useBalance(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용 금액은 양수여야 합니다.");
    }

    @Test
    void 유효하지_않은_초기값으로_생성_시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> new User("", "김철수", BigDecimal.valueOf(1000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("UUID는 필수입니다.");

        assertThatThrownBy(() -> new User("user-123", "", BigDecimal.valueOf(1000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이름은 필수입니다.");

        assertThatThrownBy(() -> new User("user-123", "김철수", BigDecimal.valueOf(-1000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("초기 잔액은 0 이상이어야 합니다.");
    }
}