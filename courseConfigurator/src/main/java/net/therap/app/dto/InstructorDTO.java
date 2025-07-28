package net.therap.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Data
public class InstructorDTO {
    
    private long id;
    
    @NotBlank
    @Size(min = 1, max = 10, message = "name length must be between {2} and {1}")
    private String name;
    
    @Email
    @NotBlank
    private String email;
    
    @NotNull
    @Past
    private LocalDate dateOfBirth;
    
    @Size(max = 255)
    private String imageUrl;
 
//    List<CourseDTO> courses;
}