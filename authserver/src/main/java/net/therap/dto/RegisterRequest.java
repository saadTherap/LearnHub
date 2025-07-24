package net.therap.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Data
public class RegisterRequest {
    
    @NotBlank
    private String name;
    
    @Email
    private String email;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String role;
}