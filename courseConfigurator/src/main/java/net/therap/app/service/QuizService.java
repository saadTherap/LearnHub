package net.therap.app.service;

import net.therap.app.model.Quiz;
import net.therap.app.repository.QuizRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
@Transactional(readOnly = true)
public class QuizService {
    
    private final QuizRepository quizRepository;
    
    private final MessageSource messageSource;
    
    public QuizService(QuizRepository quizRepository, MessageSource messageSource) {
        this.quizRepository = quizRepository;
        this.messageSource = messageSource;
    }
    
    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }
    
    public Optional<Quiz> findById(long id) {
        return quizRepository.findById(id);
    }
    
    @Transactional
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    @Transactional
    public Quiz deleteById(long id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        
        if (quiz.isPresent()) {
            quiz.get().setDeleted(true);
            return quizRepository.save(quiz.get());
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.quiz", null, Locale.getDefault()));
    }
}