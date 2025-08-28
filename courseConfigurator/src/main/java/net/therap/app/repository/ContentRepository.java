package net.therap.app.repository;

import net.therap.app.model.Content;
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

    @Override
    @Query("FROM Content c WHERE c.isDeleted = false")
    List<Content> findAll();

    @Query("SELECT DISTINCT c FROM Content c JOIN FETCH c.currentContentRelease WHERE c.module.id = :moduleId AND c.isDeleted = false")
    List<Content> findContentReleaseByModuleId(@Param("moduleId") long moduleId);
    
    @Query("SELECT c FROM Content c JOIN FETCH c.contentReleases cr WHERE cr.id = :contentReleaseId")
    Optional<Content> findContentByContentReleaseId(@Param("contentReleaseId") long contentReleaseId);

    @Query("SELECT c FROM Content c LEFT JOIN FETCH c.contentReleases r WHERE r.id = :contentReleaseId AND c.isDeleted = false")
    Optional<Content> findByContentReleaseIdWithReleases(@Param("contentReleaseId") long contentReleaseId);

    boolean existsByIdAndModuleCourseInstructorId(long contentId, long instructorId);
    
    boolean existsByIdAndModuleCourseInstructorEmail(long id, String moduleCourseInstructorEmail);
}
