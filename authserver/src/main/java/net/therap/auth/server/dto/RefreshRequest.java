package net.therap.auth.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
* @author apurboturjo
* @since 9/7/25
*/
@Data
public class RefreshRequest {
    
    @NotBlank(message = "{token.refresh.notBlank}")
    private String refreshToken;
}