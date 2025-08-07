package net.therap.learningProcessor.dto.content;

import lombok.Data;

/**
 * @author avidewan
 * @since 7/27/25
 */

@Data
public class QuizOptionDto {

    private long id;

    private String optionText;

    private long quizQuestionId;

    private boolean correct;
}