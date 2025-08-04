package net.therap.learningProcessor.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NonNull;
import net.therap.learningProcessor.annotation.EnumValidator;
import net.therap.learningProcessor.eum.Gender;
import net.therap.learningProcessor.validator.group.OnCreate;
import net.therap.learningProcessor.validator.group.OnUpdate;

import java.time.LocalDate;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
public class StudentDto {

    private Long id;

    @NotBlank(message = "{student.firstName.notBlank}", groups = OnCreate.class)
    @Size(min = 3, max = 50, message = "{student.firstName.size}", groups = {OnCreate.class, OnUpdate.class})
    private String firstName;

    @NotBlank(message = "{student.lastName.notBlank}", groups = OnCreate.class)
    @Size(min = 3, max = 50, message = "{student.lastName.size}", groups = {OnCreate.class, OnUpdate.class})
    private String lastName;

    @NotBlank(message = "{student.gender.notBlank}", groups = OnCreate.class)
    @EnumValidator(enumClass = Gender.class, message = "{student.gender.invalid}", groups = {OnCreate.class, OnUpdate.class})
    private String gender;

    @Past(message = "{student.dateOfBirth.past}", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(\\+8801|01)[0-9]{9}$", message = "{student.phone.pattern}", groups = {OnCreate.class, OnUpdate.class})
    private String phone;

    @NotBlank(message = "{student.email.notBlank}", groups = OnCreate.class)
    @Email(message = "{student.email.invalid}", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    private String address;

    private String imageUrl;
}