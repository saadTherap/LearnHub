package net.therap.learningProcessor.repository;

import net.therap.learningProcessor.entity.StudentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Repository
public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long> {

    List<StudentSubmission> findAllByStudentIdOrderBySubmittedAtDesc(Long studentId);

    List<StudentSubmission> findAllByContentIdOrderBySubmittedAtDesc(Long contentId);

    List<StudentSubmission> findAllByStudentIdAndContentIdOrderBySubmittedAtDesc(Long studentId, Long contentId);

    Optional<StudentSubmission> findFirstByStudentIdAndContentIdOrderBySubmittedAtDesc(Long studentId, Long contentId);

    @Query(value = """
        SELECT ss.*
        FROM avi_student_submission ss
        INNER JOIN (
            SELECT student_id, MAX(submitted_at) AS latest_time
            FROM avi_student_submission
            WHERE content_id = :contentId
            GROUP BY student_id
        ) grouped
        ON ss.student_id = grouped.student_id AND ss.submitted_at = grouped.latest_time
        WHERE ss.content_id = :contentId
        """, nativeQuery = true)
    List<StudentSubmission> findLatestSubmissionPerStudentByContentId(@Param("contentId") Long contentId);
}