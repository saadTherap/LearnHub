package net.therap.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author gazizafor
 * @since 7/8/25
 */
@Data
public class StudentSubmissionDTO {
    
    private Long id;
    
    private Long studentId;
    
    private Long contentId;
    
    private String studentName;
    
    private String originalFileName;
    
    private String downloadUrl;
    
    private LocalDateTime submittedAt;
}