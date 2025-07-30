package net.therap.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Data
public class InstructorDTO implements Serializable {
    
    private long id;
    
    @NotBlank(message = "{validation.name.notblank}")
    @Size(min = 2, max = 30, message = "{validation.name.size}")
    private String name;
    
    @Email(message = "{validation.email.format}")
    @NotBlank(message = "{validation.email.notblank}")
    @Size(max = 255, message = "{validation.email.size}")
    private String email;
    
    @NotNull(message = "{validation.dateOfBirth.notnull}")
    @Past(message = "{validation.dateOfBirth.past}")
    private LocalDate dateOfBirth;
    
    @Size(max = 255, message = "{validation.url.size}")
    private String imageUrl;
}