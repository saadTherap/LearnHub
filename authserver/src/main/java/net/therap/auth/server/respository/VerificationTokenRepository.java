<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/respository/VerificationTokenRepository.java
package net.therap.auth.server.respository;

import net.therap.auth.server.entity.User;
import net.therap.auth.server.entity.VerificationToken;
========
package net.therap.server.app.respository;

import net.therap.server.app.entity.User;
import net.therap.server.app.entity.VerificationToken;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/respository/VerificationTokenRepository.java
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