package net.therap.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizOptionDTO implements Serializable {
    private long id;
    private String optionText;
    private boolean isCorrect;
    private long quizQuestionId;
}