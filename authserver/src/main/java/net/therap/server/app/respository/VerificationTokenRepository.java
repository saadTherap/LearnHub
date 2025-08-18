package net.therap.server.app.respository;

import net.therap.server.app.entity.User;
import net.therap.server.app.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author apurboturjo
 * @since 7/29/25
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    
    Optional<VerificationToken> findByToken(String token);
    
    void deleteByUser(User user);
}