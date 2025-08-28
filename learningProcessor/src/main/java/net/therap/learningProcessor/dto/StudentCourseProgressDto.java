package net.therap.learningProcessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author avidewan
 * @since 7/28/25
 */
@Data
@AllArgsConstructor
public class StudentCourseProgressDto implements Serializable {

    private Long studentId;

    private String firstName;

    private String lastName;

    private String email;

    private Long courseId;

    private String courseName;

    private double progress;
}