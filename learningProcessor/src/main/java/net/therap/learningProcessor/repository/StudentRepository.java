package net.therap.learningProcessor.repository;

import net.therap.learningProcessor.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author avidewan
 * @since 7/27/25
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

}