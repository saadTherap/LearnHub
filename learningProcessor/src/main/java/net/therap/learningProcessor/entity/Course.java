package net.therap.learningProcessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * @author avidewan
 * @since 8/16/25
 */

@Table(name = "final_learnhub_course")
@Entity(name = "Course")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course {

    @Id
    private long id;

    @Column(name = "instructor_id", nullable = false)
    private long instructorId;
}