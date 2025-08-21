package net.therap.app.repository;

import net.therap.app.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author gazizafor
 * @since 21/8/25
 */
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    
    boolean existsCourseEnrollmentByCourseIdAndStudent_Email(long courseId, String studentEmail);
}