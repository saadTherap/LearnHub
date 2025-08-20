package net.therap.app.repository;

import net.therap.app.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Override
    @Query("FROM Quiz q WHERE q.isDeleted = false")
    List<Quiz> findAll();
}