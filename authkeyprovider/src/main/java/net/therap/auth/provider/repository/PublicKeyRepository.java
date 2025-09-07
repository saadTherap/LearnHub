package net.therap.auth.provider.repository;

import net.therap.auth.provider.record.PublicKeyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
public interface PublicKeyRepository extends JpaRepository<PublicKeyRecord, Long> {
    
    Optional<PublicKeyRecord> findByKid(String kid);
    
    List<PublicKeyRecord> findAllByStatus(String status);
}