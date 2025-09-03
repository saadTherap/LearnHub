package net.therap.auth.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author apurboturjo
 * @since 8/31/25
 */
@Data
public class UpdateUserRequest {
    
    @NotNull(message = "{user.id.notNull}")
    private Long id;
    
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$", message = "{user.password.strong}")
    private String password;
    
    @NotBlank(message = "{user.role.notblank}")
    private String role;
    
    private boolean enabled;
}