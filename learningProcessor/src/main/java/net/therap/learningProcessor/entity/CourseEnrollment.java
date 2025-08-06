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
@Table(name = "avi_course_enrollment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
public class CourseEnrollment extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_enrollment_seq_gen")
    @SequenceGenerator(
            name = "course_enrollment_seq_gen",
            sequenceName = "avi_course_enrollment_seq",
            allocationSize = 1
    )
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "course_id", nullable = false)
    private long courseId;
}