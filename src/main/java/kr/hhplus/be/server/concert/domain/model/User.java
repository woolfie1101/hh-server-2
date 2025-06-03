package kr.hhplus.be.server.concert.domain.model;

import java.math.BigDecimal;

/**
 * 사용자 도메인 모델 (순수 POJO)
 * - 프레임워크에 의존하지 않는 순수한 비즈니스 로직
 */
public class User {

    private Long id;
    private String uuid;
    private String name;
    private BigDecimal balance;

    // 기본 생성자 (JPA용)
    protected User() {}

    // 비즈니스 생성자
    public User(String uuid, String name, BigDecimal balance) {
        validateUuid(uuid);
        validateName(name);
        validateBalance(balance);

        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
    }

    /**
     * 잔액 충전
     */
    public void chargeBalance(BigDecimal amount) {
        validateAmount(amount, "충전 금액");
        this.balance = this.balance.add(amount);
    }

    /**
     * 잔액 사용
     * @param amount 사용할 금액
     * @return 사용 성공 여부
     */
    public boolean useBalance(BigDecimal amount) {
        validateAmount(amount, "사용 금액");

        if (!hasEnoughBalance(amount)) {
            return false;
        }
        this.balance = this.balance.subtract(amount);
        return true;
    }

    /**
     * 잔액 충분 여부 확인
     */
    public boolean hasEnoughBalance(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        return this.balance.compareTo(amount) >= 0;
    }

    // ID 할당 (Repository에서 사용)
    public void assignId(Long id) {
        this.id = id;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    // 검증 메서드들
    private void validateUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID는 필수입니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
    }

    private void validateBalance(BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("초기 잔액은 0 이상이어야 합니다.");
        }
    }

    private void validateAmount(BigDecimal amount, String fieldName) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + "은 양수여야 합니다.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User user = (User) obj;
        return uuid != null ? uuid.equals(user.uuid) : user.uuid == null;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", uuid='" + uuid + '\'' +
            ", name='" + name + '\'' +
            ", balance=" + balance +
            '}';
    }
}