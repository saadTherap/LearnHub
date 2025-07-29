package net.therap.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
    private Long quizReleaseId;       // <<< CHANGED: Parent QuizRelease's ID
    private Long quizReleaseNum;      // <<< CHANGED: Parent QuizRelease's release number
    private List<QuizOptionDTO> options; // List of options for this question
}