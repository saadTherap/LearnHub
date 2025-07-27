package net.therap.app.service;

import net.therap.app.model.Quiz;
import net.therap.app.model.QuizQuestion;
import net.therap.app.repository.QuizQuestionRepository;
import net.therap.app.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
public class QuizQuestionService {
    
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;
    
    @Autowired
    private QuizRepository quizRepository;
    
    public List<QuizQuestion> findAll() {
        return quizQuestionRepository.findAll();
    }
    
    public Optional<QuizQuestion> findById(Long id) {
        return quizQuestionRepository.findById(id);
    }
    
    public QuizQuestion save(QuizQuestion quizQuestion) {
        return quizQuestionRepository.save(quizQuestion);
    }
    
    public QuizQuestion createQuizQuestion(QuizQuestion quizQuestion, long quizId) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (quizOptional.isPresent()) {
            quizQuestion.setQuiz(quizOptional.get());
            return quizQuestionRepository.save(quizQuestion);
        }
        throw new RuntimeException("Quiz not found with ID: " + quizId);
    }
    
    public QuizQuestion updateQuizQuestion(Long id, QuizQuestion quizQuestionDetails) {
        Optional<QuizQuestion> quizQuestionOptional = quizQuestionRepository.findById(id);
        if (quizQuestionOptional.isPresent()) {
            QuizQuestion existingQuizQuestion = quizQuestionOptional.get();
            existingQuizQuestion.setQuestionText(quizQuestionDetails.getQuestionText());
            return quizQuestionRepository.save(existingQuizQuestion);
        }
        throw new RuntimeException("Quiz Question not found with ID: " + id);
    }
    
    public void deleteById(Long id) {
        quizQuestionRepository.deleteById(id);
    }
}