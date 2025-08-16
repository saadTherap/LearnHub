package net.therap.secureFileServer.repository.primary;

import net.therap.secureFileServer.entity.primary.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author avidewan
 * @since 7/22/25
 */
public interface FileRepository extends JpaRepository<StoredFile, Long> {

}