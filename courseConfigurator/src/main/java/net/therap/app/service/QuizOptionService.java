package net.therap.app.service;

import net.therap.app.model.QuizOption;
import net.therap.app.model.QuizQuestion;
import net.therap.app.repository.QuizOptionRepository;
import net.therap.app.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
@Transactional(readOnly = true)
public class QuizOptionService {
    
    private final QuizOptionRepository quizOptionRepository;
    
    public QuizOptionService(QuizOptionRepository quizOptionRepository) {
        this.quizOptionRepository = quizOptionRepository;
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
    public QuizOption delete(QuizOption option) {
        option.setDeleted(true);
        return quizOptionRepository.save(option);
    }
}