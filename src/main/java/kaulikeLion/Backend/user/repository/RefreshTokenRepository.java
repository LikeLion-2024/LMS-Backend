package kaulikeLion.Backend.user.repository;

import kaulikeLion.Backend.user.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken); //reissue
    boolean existsById(String username);
    void deleteById(String username);
}
