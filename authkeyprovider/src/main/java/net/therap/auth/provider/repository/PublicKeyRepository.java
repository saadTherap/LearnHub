package net.therap.auth.provider.repository;

import net.therap.auth.provider.entity.PublicKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
public interface PublicKeyRepository extends JpaRepository<PublicKeyEntity, Long> {
    
    Optional<PublicKeyEntity> findByKid(String kid);
}