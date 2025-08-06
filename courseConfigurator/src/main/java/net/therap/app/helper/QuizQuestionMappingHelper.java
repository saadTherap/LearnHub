package net.therap.app.helper;

import net.therap.app.model.Quiz;
import net.therap.app.service.QuizService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Component
public class QuizQuestionMappingHelper {
    
    private final QuizService quizService;
    private final MessageSource messageSource;
    
    public QuizQuestionMappingHelper(QuizService quizService, MessageSource messageSource) {
        this.quizService = quizService;
        this.messageSource = messageSource;
    }
    
    public Quiz map(long quizId) {
        Optional<Quiz> quizOptional = quizService.findById(quizId);
        
        if (quizOptional.isPresent()) {
            return quizOptional.get();
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.quiz", null, Locale.getDefault()));
    }
}