package net.therap.auth.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 8/31/25
 */
@Data
public class UpdateUserRequest implements Serializable {
    
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$", message = "{user.password.strong}")
    private String password;
    
    private boolean enabled;
    
    private String updateAccessToken; // For pass reset case: Not logged-in
    
    private String loggedInAccessToken; // For account deletion request by user: logged-in
}