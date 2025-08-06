package net.therap.app.controller;

import net.therap.app.dto.QuizOptionDTO;
import net.therap.app.dto.QuizQuestionDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.QuizOptionMapper;
import net.therap.app.mapper.QuizQuestionMapper;
import net.therap.app.model.Quiz;
import net.therap.app.model.QuizOption;
import net.therap.app.model.QuizQuestion;
import net.therap.app.repository.QuizOptionRepository;
import net.therap.app.repository.QuizQuestionRepository;
import net.therap.app.repository.QuizRepository;
import net.therap.app.service.QuizOptionService;
import net.therap.app.service.QuizQuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@RestController
@RequestMapping("/api/contents/quiz/questions")
public class QuizController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizQuestionMapper quizQuestionMapper;
    private final DtoHelper dtoHelper;
    private final QuizQuestionService quizQuestionService;
    private final QuizOptionMapper quizOptionMapper;
    private final QuizOptionService quizOptionService;
    
    public QuizController(QuizRepository quizRepository, QuizQuestionRepository quizQuestionRepository, QuizOptionRepository quizOptionRepository, QuizQuestionMapper quizQuestionMapper, DtoHelper dtoHelper, QuizQuestionService quizQuestionService, QuizOptionMapper quizOptionMapper, QuizOptionService quizOptionService) {
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizOptionRepository = quizOptionRepository;
        this.quizQuestionMapper = quizQuestionMapper;
        this.dtoHelper = dtoHelper;
        this.quizQuestionService = quizQuestionService;
        this.quizOptionMapper = quizOptionMapper;
        this.quizOptionService = quizOptionService;
    }
    
    @GetMapping("")
    public ResponseEntity<List<QuizQuestionDTO>> getAllQuizQuestions() {
        return ResponseEntity.ok(quizQuestionRepository.findAll().stream().map(dtoHelper::toQuizQuestionDTO).toList());
    }
    
    @PostMapping("/new")
    public ResponseEntity<QuizQuestionDTO> createNewQuizQuestion(@RequestBody QuizQuestionDTO quizQuestionDTO) {
        QuizQuestion quizQuestion = quizQuestionMapper.toQuizQuestion(quizQuestionDTO);
        List<QuizOption> quizOptions = new ArrayList<>();
        
        logger.info("Quiz options size in body: {}", quizQuestionDTO.getOptions().size());
        
        for (QuizOptionDTO optionDTO : quizQuestionDTO.getOptions()) {
            QuizOption option = quizOptionMapper.toQuizOption(optionDTO);
            option.setQuizQuestion(quizQuestion);
            quizOptions.add(option);
        }
        
        quizQuestion.setOptions(quizOptions);
        quizQuestionService.save(quizQuestion);
        
        return ResponseEntity.ok(dtoHelper.toQuizQuestionDTO(quizQuestion));
    }
    
    @PostMapping("/delete/{questionId}")
    public ResponseEntity<QuizQuestionDTO> deleteQuizQuestion(@PathVariable long questionId) {
        return ResponseEntity.ok(dtoHelper.toQuizQuestionDTO(quizQuestionService.delete(questionId)));
    }
}