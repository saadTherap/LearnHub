package net.therap.app.service;

import net.therap.app.model.QuizOption;
import net.therap.app.model.QuizQuestion;
import net.therap.app.repository.QuizQuestionRepository;
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
public class QuizQuestionService {
    
    private final QuizQuestionRepository quizQuestionRepository;
    
    private final MessageSource messageSource;
    
    private final QuizOptionService quizOptionService;
    
    public QuizQuestionService(QuizQuestionRepository quizQuestionRepository, MessageSource messageSource, QuizOptionService quizOptionService) {
        this.quizQuestionRepository = quizQuestionRepository;
        this.messageSource = messageSource;
        this.quizOptionService = quizOptionService;
    }
    
    public List<QuizQuestion> findAll() {
        return quizQuestionRepository.findAll();
    }
    
    public Optional<QuizQuestion> findById(Long id) {
        return quizQuestionRepository.findById(id);
    }
    
    @Transactional
    public QuizQuestion save(QuizQuestion quizQuestion) {
        return quizQuestionRepository.save(quizQuestion);
    }
    
    @Transactional
    public QuizQuestion delete(long questionId) {
        Optional<QuizQuestion> quizQuestionOptional = quizQuestionRepository.findById(questionId);
        
        if (quizQuestionOptional.isPresent()) {
            quizQuestionOptional.get().setDeleted(true);
            
            for (QuizOption option : quizQuestionOptional.get().getOptions()) {
                option = quizOptionService.deleteById(option.getId());
            }
            
            return quizQuestionRepository.save(quizQuestionOptional.get());
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.quiz", null, Locale.getDefault()));
    }
}