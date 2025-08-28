package net.therap.learningProcessor.dto.content.quiz;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */

@Data
public class QuizQuestionDto implements Serializable {

    private long id;

    private String questionText;

    private long quizReleaseId;

    private int quizReleaseNum;

    private List<QuizOptionDto> options;
}