package net.therap.app.repository;

import net.therap.app.model.Module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    @Query("FROM Module m WHERE  m.course.id = :courseId")
    List<Module> findByCourseId(@Param("courseId") long courseId);
    
    @Query("SELECT MAX(m.orderIndex) FROM Course c JOIN c.modules m WHERE c.id = :courseId")
    long findMaxOrderIndexOfModules(@Param("courseId") long courseId);
    
    boolean existsByIdAndCourseInstructorId(long moduleId, long instructorId);
    
    boolean existsByIdAndCourseInstructorEmail(long id, String courseInstructorEmail);
}