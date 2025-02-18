package vn.iuh.fit.musical_instrument.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iuh.fit.musical_instrument.entites.SecureToken;

public interface SecureTokenRepository extends JpaRepository<SecureToken, Long> {
    Optional<SecureToken> findByToken(String token);
}

