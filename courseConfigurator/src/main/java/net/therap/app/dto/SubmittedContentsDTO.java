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
public class SubmittedContentsDTO {
    
    private long contentId;
    
    // contains student info and the latest file submitted by those students
    private List<StudentSubmissionDTO> latestSubmissions;
}