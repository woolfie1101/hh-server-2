package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 레포지토리 인터페이스
 */
@Repository
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsById(UUID id);
    void deleteById(UUID id);
}