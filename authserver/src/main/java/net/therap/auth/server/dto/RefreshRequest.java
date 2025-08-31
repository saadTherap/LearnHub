package net.therap.auth.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
@Data
public class RefreshRequest implements Serializable {
    
    @NotBlank
    private String refreshToken;
}