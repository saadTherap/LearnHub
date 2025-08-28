package net.therap.app.repository;

import net.therap.app.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    @Override
    @Query("FROM Lecture l WHERE l.isDeleted = false")
    List<Lecture> findAll();
}