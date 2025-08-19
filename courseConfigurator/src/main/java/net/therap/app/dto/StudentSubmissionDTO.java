package net.therap.app.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author gazizafor
 * @since 7/8/25
 */
@Data
public class StudentSubmissionDTO implements Serializable {
    
    private Long id;
    
    private Long studentId;
    
    private Long contentId;
    
    private String studentName;
    
    private String originalFileName;
    
    private String downloadUrl;
    
    private LocalDateTime submittedAt;
}