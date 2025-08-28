package net.therap.app.repository;

import net.therap.app.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    @Override
    @Query("FROM QuizQuestion q WHERE q.isDeleted = false")
    List<QuizQuestion> findAll();
}