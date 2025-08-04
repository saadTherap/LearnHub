package net.therap.learningProcessor.repository;

import net.therap.learningProcessor.entity.StudentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Repository
public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long> {

    Optional<StudentSubmission> findByStudentIdAndContentId(Long studentId, Long contentId);
}