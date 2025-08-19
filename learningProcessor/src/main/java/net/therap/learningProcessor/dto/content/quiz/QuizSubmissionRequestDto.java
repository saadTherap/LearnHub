package net.therap.learningProcessor.dto.content.quiz;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author avidewan
 * @since 8/10/25
 */
@Data
public class QuizSubmissionRequestDto {

    private Long studentId;

    private Long contentId;

    private Map<Long, List<Long>> answers;

    private QuizDto quizDto;
}
