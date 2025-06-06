package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.model.User;
import kr.hhplus.be.server.repository.UserRepository;
import kr.hhplus.be.server.repository.SpringUserJpa;

import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SpringUserJpa springUserJpa;

    public UserRepositoryImpl(SpringUserJpa springUserJpa) {
        this.springUserJpa = springUserJpa;
    }

    @Override
    public User save(User user) {
        return springUserJpa.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springUserJpa.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springUserJpa.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springUserJpa.existsByEmail(email);
    }

    @Override
    public boolean existsById(UUID id) {
        return springUserJpa.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        springUserJpa.deleteById(id);
    }
} 