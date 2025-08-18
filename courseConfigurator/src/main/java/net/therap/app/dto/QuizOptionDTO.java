package net.therap.app.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizOptionDTO implements Serializable {
    private long id;
    private String optionText;
    private boolean isCorrect;
    private long quizQuestionId;
}