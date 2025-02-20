package vn.iuh.fit.musical_instrument.repository;


import java.util.Optional;
import vn.iuh.fit.musical_instrument.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String userEmail);
}

