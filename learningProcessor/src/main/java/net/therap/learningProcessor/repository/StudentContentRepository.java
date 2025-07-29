package net.therap.learningProcessor.repository;

import net.therap.learningProcessor.dto.StudentContentStatusDto;
import net.therap.learningProcessor.entity.StudentContent;
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
public interface StudentContentRepository extends JpaRepository<StudentContent, Long> {

    Optional<StudentContent> findByStudentIdAndContentId(Long studentId, Long contentId);

    @Query("SELECT new net.therap.learningProcessor.dto.StudentContentStatusDto(" +
            "sc.student.id, sc.contentId, sc.status) " +
            "FROM StudentContent sc " +
            "WHERE sc.student.id = :studentId AND sc.isDeleted = false")
    List<StudentContentStatusDto> getStudentContentStatusByStudentId(@Param("studentId") Long studentId);
}