package net.therap.app.repository;

import net.therap.app.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
//@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    @Query("FROM Course c WHERE c.currentRelease = 0")
    List<Course> findAllDrafts();
    
    List<Course> findByInstructor_Id(long instructorId);
    
    @Query("FROM Course c WHERE c.currentRelease = 0 AND c.id = :id")
    Optional<Course> findDraftById(@Param("id") long id);
}