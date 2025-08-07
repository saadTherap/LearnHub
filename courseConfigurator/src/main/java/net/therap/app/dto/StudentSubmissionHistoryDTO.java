package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gazizafor
 * @since 7/8/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSubmissionHistoryDTO {
    
    private long studentId;
    private String studentName;
    
    List<String> submissionUrls;
}