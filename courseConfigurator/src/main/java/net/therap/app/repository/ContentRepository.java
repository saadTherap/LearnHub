package net.therap.app.repository;

import net.therap.app.model.Content;
import net.therap.app.model.ContentRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 24/7/25
 */
public interface ContentRepository extends JpaRepository<Content, Long> {
    
    @Query("SELECT DISTINCT c FROM Content c JOIN FETCH c.currentContentRelease WHERE c.module.id = :moduleId")
    List<Content> findContentReleaseByModuleId(@Param("moduleId") long moduleId);
    
    @Query("SELECT c " +
            "FROM Content c JOIN FETCH c.currentContentRelease " +
            "WHERE c.currentContentRelease.id = :contentReleaseId " +
            "ORDER BY c.currentContentRelease.orderIndex ASC")
    Optional<Content> findContentByContentReleaseId(@Param("contentReleaseId") long contentReleaseId);
}
