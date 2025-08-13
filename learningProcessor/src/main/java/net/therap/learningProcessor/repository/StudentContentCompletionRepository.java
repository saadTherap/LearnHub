package net.therap.learningProcessor.repository;

import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.entity.StudentContentCompletion;
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
public interface StudentContentCompletionRepository extends JpaRepository<StudentContentCompletion, Long> {

    Optional<StudentContentCompletion> findByStudentIdAndContentId(Long studentId, Long contentId);

    @Query("SELECT new net.therap.learningProcessor.dto.StudentContentCompletionDto(" +
            "sc.student.id, sc.contentId) " +
            "FROM StudentContentCompletion sc " +
            "WHERE sc.student.id = :studentId AND sc.isDeleted = false")
    List<StudentContentCompletionDto> getStudentContentStatusByStudentId(@Param("studentId") Long studentId);
}