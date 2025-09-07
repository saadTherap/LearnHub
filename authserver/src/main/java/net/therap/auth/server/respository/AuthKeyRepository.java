package net.therap.auth.server.respository;

import net.therap.auth.server.entity.AuthKey;
import net.therap.auth.server.enums.KeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author apurboturjo
 * @since 9/7/25
 */
public interface AuthKeyRepository extends JpaRepository<AuthKey, Long> {
    
    Optional<AuthKey> findByKid(String kid);
    Optional<AuthKey> findByStatus(KeyStatus status);
}