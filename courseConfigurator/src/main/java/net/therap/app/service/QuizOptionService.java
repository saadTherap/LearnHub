package net.therap.app.service;

import net.therap.app.model.QuizOption;
import net.therap.app.model.QuizQuestion;
import net.therap.app.repository.QuizOptionRepository;
import net.therap.app.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class QuizOptionService {
    
    private final QuizOptionRepository quizOptionRepository;
    private final MessageSource messageSource;
    
    public QuizOptionService(QuizOptionRepository quizOptionRepository, MessageSource messageSource) {
        this.quizOptionRepository = quizOptionRepository;
        this.messageSource = messageSource;
    }
    
    public List<QuizOption> findAll() {
        return quizOptionRepository.findAll();
    }
    
    public Optional<QuizOption> findById(Long id) {
        return quizOptionRepository.findById(id);
    }
    
    @Transactional
    public QuizOption save(QuizOption quizOption) {
        return quizOptionRepository.save(quizOption);
    }
    
    @Transactional
    public QuizOption deleteById(long id) {
        Optional<QuizOption> option = quizOptionRepository.findById(id);
        
        if (option.isPresent()) {
            option.get().setDeleted(true);
            return quizOptionRepository.save(option.get());
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.option", null, Locale.getDefault()));
    }
}