package net.therap.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import net.therap.app.validation.annotations.UniqueEmail;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Data
public class InstructorDTO implements Serializable {
    
    private long id;
    
    @NotBlank(message = "{validation.name.notblank}", groups = OnCreate.class)
    @Size(min = 2, max = 30, message = "{validation.name.size}", groups = {OnCreate.class, OnUpdate.class})
    private String name;
    
    @Email(message = "{validation.email.format}",  groups = {OnCreate.class})
    @NotBlank(message = "{validation.email.notblank}", groups = {OnCreate.class})
    @Size(max = 255, message = "{validation.email.size}", groups = {OnCreate.class})
    @UniqueEmail(groups = {OnCreate.class, OnUpdate.class})
    private String email;
    
    @NotNull(message = "{validation.dateOfBirth.notnull}",  groups = {OnCreate.class})
    @Past(message = "{validation.dateOfBirth.past}",  groups = {OnCreate.class, OnUpdate.class})
    private LocalDate dateOfBirth;
    
    @Size(max = 255, message = "{validation.url.size}",  groups = {OnCreate.class,  OnUpdate.class})
    private String imageUrl;
}