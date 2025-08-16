package net.therap.server.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
@Data
public class RefreshRequest implements Serializable {
    
    private String refreshToken;
}