package net.therap.app.dto;

/**
 * @author gazizafor
 * @since 21/7/25
 */
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List; // For collections if you decide to include simplified course info

/**
 * Data Transfer Object (DTO) for Instructor entities.
 * Used to expose instructor information through the API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO { // Renamed from InstructorDTO to TeacherDTO as per request
    
    private long id;
    private String name;
    // private String email; // Only include if email is public information
    private Date createdAt;
    private Date updatedAt;
    // private List<CourseDTO> courses; // Optional: If you want to show a list of simplified courses
    // Ensure CourseDTO does NOT link back to TeacherDTO to prevent recursion.
}