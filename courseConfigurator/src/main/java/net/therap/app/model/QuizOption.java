package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Entity
@Table(name = "final_learnhub_quiz_option")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizOption extends Persistent{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_learnhub_quiz_option_seq_gen")
    @SequenceGenerator(name = "final_learnhub_quiz_option_seq_gen", sequenceName = "final_learnhub_quiz_option_seq", allocationSize = 1)
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false)
    private QuizQuestion quizQuestion;
    
    @Column(name = "option_text", nullable = false, length = 256)
    private String optionText;
    
    @Column(name = "is_correct", columnDefinition = "NUMBER(1) DEFAULT 0")
    private boolean isCorrect;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (o instanceof QuizOption other) {
            
            return this.id == other.id;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}