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
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Override
    @Query("FROM Course c WHERE c.isDeleted = false")
    List<Course> findAll();
    
    @Override
    @Query("FROM Course c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Course> findById(@Param("id") Long id);
    
    @Query("FROM Course c WHERE c.currentRelease = 0 AND c.instructor.id = :instructorId AND c.isDeleted = false")
    List<Course> findAllDrafts(@Param("instructorId") long instructorId);

    @Query("FROM Course c WHERE c.currentRelease = 0 AND c.isDeleted = false")
    List<Course> findAllDrafts();

    @Query("FROM Course c WHERE c.instructor.id = :instructorId AND c.isDeleted = false")
    List<Course> findByInstructor_Id(@Param("instructorId") long instructorId);

    @Query("FROM Course c WHERE c.currentRelease = 0 AND c.id = :id AND c.isDeleted = false")
    Optional<Course> findDraftById(@Param("id") long id);
    
    boolean existsByIdAndInstructorId(long courseId, long instructorId);
    
    boolean existsByIdAndInstructorEmail(@Param("id") long courseId, @Param("email") String email);
}