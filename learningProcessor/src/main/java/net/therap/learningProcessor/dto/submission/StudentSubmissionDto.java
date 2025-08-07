package net.therap.learningProcessor.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author avidewan
 * @since 8/7/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentSubmissionDto {

    private Long id;

    private Long studentId;

    private Long contentId;

    private String studentName;

    private String originalFileName;

    private String downloadUrl;

    private LocalDateTime submittedAt;
}
