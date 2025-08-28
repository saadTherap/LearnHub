package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Entity
@Table(name = "final_learnhub_quiz")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("QUIZ")
public class Quiz extends ContentRelease {
    
    @OneToMany(mappedBy = "quiz", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizQuestion> questions;

    public List<QuizQuestion> getQuestions() {
        return questions.stream()
                .filter(quizQuestion -> !quizQuestion.isDeleted())
                .toList();
    }
}