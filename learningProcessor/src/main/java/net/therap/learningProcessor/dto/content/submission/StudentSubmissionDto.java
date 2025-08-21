package net.therap.learningProcessor.dto.content.submission;

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

    private String formId;

    private String downloadUrl;

    private String originalFileName;

    private LocalDateTime submittedAt;

    private String contentType;

    private String uploaderEmail;

    private String studentName;
}