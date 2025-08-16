package net.therap.secureFileServer.repository.primary;

import net.therap.secureFileServer.entity.primary.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, long courseId);
}