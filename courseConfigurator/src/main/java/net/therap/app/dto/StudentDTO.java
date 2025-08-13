package net.therap.app.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Data
public class StudentDTO implements Serializable {
    
    private long id;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String address;
    private String imageUrl;
}