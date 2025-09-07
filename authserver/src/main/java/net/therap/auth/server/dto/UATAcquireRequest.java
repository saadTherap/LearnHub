package net.therap.auth.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 9/4/25
 */
@Data
public class UATAcquireRequest implements Serializable {
    
    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.notblank}")
    private String email;
}