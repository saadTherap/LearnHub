package net.therap.auth.server.respository;

import net.therap.auth.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByEmail(String email);
}