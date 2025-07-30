package net.therap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author apurboturjo
 * @since 7/30/25
 */
@Data
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    
    private String message;
}