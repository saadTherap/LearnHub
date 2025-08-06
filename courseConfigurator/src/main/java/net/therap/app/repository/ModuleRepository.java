package net.therap.app.repository;

import net.therap.app.model.Module;

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
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    @Query("FROM final_learnhub_module m WHERE  m.course.id = :courseId")
    List<Module> findByCourseId(@Param("courseId") long courseId);
}