package net.therap.learningProcessor.repository;

import net.therap.learningProcessor.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<CourseEnrollment> findByCourseId(long courseId);

    List<CourseEnrollment> findByStudentId(long studentId);

    void deleteAllByStudentId(long studentId);

    void deleteByStudentIdAndCourseId(long studentId, long courseId);
}
