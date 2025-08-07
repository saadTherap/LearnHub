package net.therap.app.repository;

import net.therap.app.model.ContentRelease;
import net.therap.app.model.Submission;
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
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

}