<<<<<<<< HEAD:authserver/src/main/java/net/therap/auth/server/dto/LoginRequest.java
package net.therap.auth.server.dto;
========
package net.therap.server.app.dto;
>>>>>>>> 54db752 (Updated the auth):authserver/src/main/java/net/therap/server/app/dto/LoginRequest.java

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Data
public class LoginRequest implements Serializable {
    
    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.notblank}")
    private String email;
    
    @NotBlank(message = "{user.password.notblank}")
    @Size(min = 6, message = "{user.password.size}")
    private String password;
}