package net.therap.app.repository;

import net.therap.app.model.Content;
import net.therap.app.model.ContentRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Repository
public interface ContentReleaseRepository extends JpaRepository<ContentRelease, Long> {

}
