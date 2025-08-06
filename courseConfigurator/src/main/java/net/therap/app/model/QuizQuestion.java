package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Entity
@Table(name = "final_learnhub_quiz_question")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizQuestion extends Persistent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_learnhub_quiz_ques_seq_gen")
    @SequenceGenerator(name = "final_learnhub_quiz_ques_seq_gen", sequenceName = "final_learnhub_quiz_ques_seq",
            allocationSize = 1)
    @Column(name = "id")
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id", nullable = false)
    private Quiz quiz;
    
    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;
    
    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizOption> options;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (o instanceof QuizQuestion that) {
            
            return this.id == that.id;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}