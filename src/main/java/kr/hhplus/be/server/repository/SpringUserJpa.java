package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.model.User;
import kr.hhplus.be.server.entity.UserEntity;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 JPA 레포지토리
 */
@Repository
public class SpringUserJpa implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public SpringUserJpa(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.fromDomain(user);
        entity = userJpaRepository.save(entity);
        return entity.toDomain();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
            .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
            .map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(UUID id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id);
    }
} 