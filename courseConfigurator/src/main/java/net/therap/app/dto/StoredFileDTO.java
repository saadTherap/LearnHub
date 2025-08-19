package net.therap.app.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author gazizafor
 * @since 13/8/25
 */
@Data
@Builder
public class StoredFileDTO implements Serializable {
    
    private Long id;
    
    private String originalFilename;
    
    private String contentType;
    
    private String downloadUrl;
    
    private LocalDateTime uploadTime;
}