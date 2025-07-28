package net.therap.learningProcessor.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
public class StudentDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private String address;
    private String imageUrl;
}