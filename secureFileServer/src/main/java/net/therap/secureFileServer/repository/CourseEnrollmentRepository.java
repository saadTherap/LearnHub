package net.therap.secureFileServer.repository;

import net.therap.secureFileServer.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, long courseId);
}