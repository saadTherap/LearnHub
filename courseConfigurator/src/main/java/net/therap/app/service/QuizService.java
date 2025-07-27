package net.therap.app.service;

import net.therap.app.model.Module;
import net.therap.app.model.Quiz;
import net.therap.app.repository.ModuleRepository;
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
public class QuizService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }
    
    public Optional<Quiz> findById(long id) {
        return quizRepository.findById(id);
    }
    
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    public Quiz createQuiz(Quiz quiz, Long moduleId) {
        Optional<Module> moduleOptional = moduleRepository.findById(moduleId);
        if (moduleOptional.isPresent()) {
            quiz.getContent().setModule(moduleOptional.get());
            return quizRepository.save(quiz);
        }
        throw new RuntimeException("Module not found with ID: " + moduleId);
    }
    
    public Quiz updateQuiz(long id, Quiz quizDetails) {
        Optional<Quiz> quizOptional = quizRepository.findById(id);
        if (quizOptional.isPresent()) {
            Quiz existingQuiz = quizOptional.get();
            existingQuiz.getContent().setTitle(quizDetails.getContent().getTitle());
            return quizRepository.save(existingQuiz);
        }
        throw new RuntimeException("Quiz not found with ID: " + id);
    }
    
    public void deleteById(long id) {
        quizRepository.deleteById(id);
    }
}