package kaulikeLion.Backend.oauth.repository;

import kaulikeLion.Backend.oauth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 1. 사용자 계정이름으로 사용자 정보를 회수하는 기능
    Optional<User> findByUsername(String username);
    // 2. 사용자 계정이름을 가진 사용자 정보가 존재하는지 판단하는 기능
    boolean existsByUsername(String username);
    // 3. 사용자 이메일을 가진 사용자 정보가 존재하는지 판단하는 기능
    boolean existsByEmail(String email);

}
