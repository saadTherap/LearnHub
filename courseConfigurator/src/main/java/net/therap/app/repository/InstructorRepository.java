package net.therap.app.repository;

import net.therap.app.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    
    @Query("FROM Instructor i WHERE i.email = :email")
    Optional<Instructor> findByEmail(@Param("email") String email);
    
    boolean existsByEmail(String email);
}