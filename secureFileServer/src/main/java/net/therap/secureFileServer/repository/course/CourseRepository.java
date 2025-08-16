package net.therap.secureFileServer.repository.course;

import net.therap.secureFileServer.entity.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author avidewan
 * @since 8/16/25
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

}