package net.therap.auth.server.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author apurboturjo
 * @since 8/14/25
 */
@Data
public class DeleteRequest implements Serializable {
    
    private String accessToken;
}