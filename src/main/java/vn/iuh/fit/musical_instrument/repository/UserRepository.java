package vn.iuh.fit.musical_instrument.repository;


import java.util.Optional;
import vn.iuh.fit.musical_instrument.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserEmail(String userEmail);
}

