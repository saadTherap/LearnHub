package net.therap.learningProcessor.dto.content.quiz;

import lombok.Data;

import java.io.Serializable;

/**
 * @author avidewan
 * @since 7/27/25
 */

@Data
public class QuizOptionDto implements Serializable {

    private long id;

    private String optionText;

    private long quizQuestionId;

    private boolean correct;
}