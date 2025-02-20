package vn.iuh.fit.musical_instrument.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iuh.fit.musical_instrument.entities.RefreshToken;
import vn.iuh.fit.musical_instrument.entities.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
