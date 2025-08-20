package net.therap.app.repository;

import net.therap.app.model.ContentRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Repository
public interface ContentReleaseRepository extends JpaRepository<ContentRelease, Long> {

    @Override
    @Query("FROM ContentRelease cr WHERE cr.isDeleted = false")
    List<ContentRelease> findAll();

    @Query("SELECT cr FROM Content c JOIN c.currentContentRelease cr WHERE c.module.course.instructor.id = :instructorId AND cr.isDeleted = false")
    List<ContentRelease> findByInstructorId(@Param("instructorId") long instructorId);

    boolean existsByIdAndContentModuleCourseInstructorEmail(long id, String contentModuleCourseInstructorEmail);
}
