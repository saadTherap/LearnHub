package net.therap.learningProcessor.repository;

import net.therap.learningProcessor.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author avidewan
 * @since 7/27/25
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);
}