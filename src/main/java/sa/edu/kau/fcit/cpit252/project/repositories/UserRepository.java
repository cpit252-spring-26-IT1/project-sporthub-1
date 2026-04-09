package sa.edu.kau.fcit.cpit252.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sa.edu.kau.fcit.cpit252.project.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}