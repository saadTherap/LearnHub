package net.therap.learningProcessor.dto.content;

import lombok.Data;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */

@Data
public class QuizQuestionDto {

    private long id;
    private String questionText;
    private long quizReleaseId;
    private int quizReleaseNum;
    private List<QuizOptionDto> options;
}