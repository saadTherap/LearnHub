package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO extends ContentReleaseDTO {
    private List<QuizQuestionDTO> questions;
    
    public QuizDTO(long id, long releaseNum, long orderedIndex, String title, long contentId,
                   List<QuizQuestionDTO> questions) {
        super(id, releaseNum, orderedIndex, title, contentId, "QUIZ");
        this.questions = questions;
    }
}