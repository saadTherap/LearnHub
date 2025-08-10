package net.therap.learningProcessor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author avidewan
 * @since 7/24/25
 */

@Getter
@Setter
@Entity
@Table(name = "avi_student_submission",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "content_id"}))
public class StudentSubmission extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_submission_seq_gen")
    @SequenceGenerator(
            name = "student_submission_seq_gen",
            sequenceName = "avi_student_submission_seq",
            allocationSize = 1
    )
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "content_id", nullable = false)
    private long contentId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "download_url")
    private String downloadUrl;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
}