package net.therap.learningProcessor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author avidewan
 * @since 7/24/25
 */

@Getter
@Setter
@Entity
@Table(name = "avi_student_content_completion")
public class StudentContentCompletion extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_content_seq_gen")
    @SequenceGenerator(
            name = "student_content_seq_gen",
            sequenceName = "avi_student_content_seq",
            allocationSize = 1
    )
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "content_id", nullable = false)
    private long contentId;
}