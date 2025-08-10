package net.therap.learningProcessor.dto.content.quiz;

import lombok.Builder;
import lombok.Data;

/**
 * @author avidewan
 * @since 8/10/25
 */
@Data
@Builder
public class QuizSubmissionResultDto {

    private Long studentId;

    private Long contentId;

    private double scorePercentage;

    private boolean passed;
}