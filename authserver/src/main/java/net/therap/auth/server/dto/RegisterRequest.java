<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/dto/RegisterRequest.java
package net.therap.auth.server.dto;
========
package net.therap.server.app.dto;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/dto/RegisterRequest.java

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Data
public class RegisterRequest implements Serializable {
    
    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.notblank}")
    private String email;
    
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$", message = "{user.password.strong}")
    private String password;
    
    @NotBlank(message = "{user.role.notblank}")
    private String role;
}