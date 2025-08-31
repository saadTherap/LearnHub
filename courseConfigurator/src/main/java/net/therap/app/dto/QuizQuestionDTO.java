package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDTO implements Serializable {
    private Long id;
    private String questionText;
    private Long quizId;
    
    private List<QuizOptionDTO> options;
}