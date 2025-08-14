package net.therap.server.app.dto;

import lombok.Data;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
@Data
public class RefreshRequest {
    
    private String refreshToken;
}