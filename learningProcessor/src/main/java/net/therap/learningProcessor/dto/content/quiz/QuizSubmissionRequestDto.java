package net.therap.learningProcessor.dto.content.quiz;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author avidewan
 * @since 8/10/25
 */
@Data
public class QuizSubmissionRequestDto implements Serializable {

    private Long studentId;

    private Long contentId;

    private Map<Long, List<Long>> answers;

    private QuizDto quiz;
}
