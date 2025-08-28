package net.therap.learningProcessor.repository;


import net.therap.learningProcessor.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author avidewan
 * @since 8/21/25
 */
public interface CourseRepository extends JpaRepository<Course, Long> {
}