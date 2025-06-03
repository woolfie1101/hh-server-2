package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.User;
import kr.hhplus.be.server.concert.domain.repository.UserRepository;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.UserEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringUserJpa;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserRepository 구현체
 * - 도메인 Repository 인터페이스의 실제 구현
 * - Spring Data JPA와 도메인 모델 간의 어댑터 역할
 * - Entity ↔ Domain Model 변환 담당
 */
@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {

    private final SpringUserJpa springUserJpa;

    public UserRepositoryImpl(SpringUserJpa springUserJpa) {
        this.springUserJpa = springUserJpa;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = UserEntity.fromDomain(user);
        UserEntity savedEntity = springUserJpa.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return springUserJpa.findById(id)
            .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        return springUserJpa.findByUuid(uuid)
            .map(UserEntity::toDomain);
    }

    @Override
    public List<User> findByName(String name) {
        return springUserJpa.findByName(name)
            .stream()
            .map(UserEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findByBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance) {
        return springUserJpa.findByBalanceBetween(minBalance, maxBalance)
            .stream()
            .map(UserEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findByBalanceGreaterThanEqual(BigDecimal minBalance) {
        return springUserJpa.findByBalanceGreaterThanEqual(minBalance)
            .stream()
            .map(UserEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findByCreatedAtAfter(LocalDateTime createdAt) {
        return springUserJpa.findByCreatedAtAfter(createdAt)
            .stream()
            .map(UserEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUuid(String uuid) {
        return springUserJpa.existsByUuid(uuid);
    }

    @Override
    @Transactional
    public int deleteByUuid(String uuid) {
        return springUserJpa.deleteByUuid(uuid);
    }

    @Override
    public BigDecimal calculateTotalBalance() {
        return springUserJpa.calculateTotalBalance();
    }

    @Override
    public BigDecimal calculateAverageBalance() {
        return springUserJpa.calculateAverageBalance();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        springUserJpa.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return springUserJpa.findAll()
            .stream()
            .map(UserEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return springUserJpa.count();
    }
}