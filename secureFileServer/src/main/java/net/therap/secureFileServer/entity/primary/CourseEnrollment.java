package net.therap.secureFileServer.entity.primary;

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
@Table(name = "avi_course_enrollment")
public class CourseEnrollment {

    @Id
    private long id;

    @JoinColumn(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;
}
