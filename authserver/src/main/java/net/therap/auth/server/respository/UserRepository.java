package net.therap.auth.server.respository;

import net.therap.auth.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("FROM User u ORDER BY u.role ASC")
    List<User> findAllSorted();
}