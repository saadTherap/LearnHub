package net.therap.app.service;

import net.therap.app.model.QuizOption;
import net.therap.app.model.QuizQuestion;
import net.therap.app.repository.QuizOptionRepository;
import net.therap.app.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
public class QuizOptionService {
    
    @Autowired
    private QuizOptionRepository quizOptionRepository;
    
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;
    
    public List<QuizOption> findAll() {
        return quizOptionRepository.findAll();
    }
    
    public Optional<QuizOption> findById(Long id) {
        return quizOptionRepository.findById(id);
    }
    
    public QuizOption save(QuizOption quizOption) {
        return quizOptionRepository.save(quizOption);
    }
    
    public QuizOption createQuizOption(QuizOption quizOption, Long quizQuestionId) {
        Optional<QuizQuestion> quizQuestionOptional = quizQuestionRepository.findById(quizQuestionId);
        if (quizQuestionOptional.isPresent()) {
            quizOption.setQuizQuestion(quizQuestionOptional.get());
            return quizOptionRepository.save(quizOption);
        }
        throw new RuntimeException("Quiz Question not found with ID: " + quizQuestionId);
    }
    
    public QuizOption updateQuizOption(Long id, QuizOption quizOptionDetails) {
        Optional<QuizOption> quizOptionOptional = quizOptionRepository.findById(id);
        if (quizOptionOptional.isPresent()) {
            QuizOption existingQuizOption = quizOptionOptional.get();
            existingQuizOption.setOptionText(quizOptionDetails.getOptionText());
            existingQuizOption.setCorrect(quizOptionDetails.isCorrect());
            return quizOptionRepository.save(existingQuizOption);
        }
        throw new RuntimeException("Quiz Option not found with ID: " + id);
    }
    
    public void deleteById(Long id) {
        quizOptionRepository.deleteById(id);
    }
}