package net.therap.secureFileServer.repository;

import net.therap.secureFileServer.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author avidewan
 * @since 7/22/25
 */
public interface FileRepository extends JpaRepository<StoredFile, Long> {
}