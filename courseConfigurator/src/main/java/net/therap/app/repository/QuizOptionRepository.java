package net.therap.app.repository;

import net.therap.app.model.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Long> {
    
}