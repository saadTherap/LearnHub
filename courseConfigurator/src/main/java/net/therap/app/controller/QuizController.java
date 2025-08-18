package net.therap.app.controller;

import lombok.extern.slf4j.Slf4j;
import net.therap.app.dto.QuizOptionDTO;
import net.therap.app.dto.QuizQuestionDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.QuizOptionMapper;
import net.therap.app.mapper.QuizQuestionMapper;
import net.therap.app.model.QuizOption;
import net.therap.app.model.QuizQuestion;
import net.therap.app.repository.QuizOptionRepository;
import net.therap.app.repository.QuizQuestionRepository;
import net.therap.app.repository.QuizRepository;
import net.therap.app.service.QuizOptionService;
import net.therap.app.service.QuizQuestionService;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Slf4j
@RestController
@RequestMapping("/contents/quiz/questions")
public class QuizController {
    
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizQuestionMapper quizQuestionMapper;
    private final DtoHelper dtoHelper;
    private final QuizQuestionService quizQuestionService;
    private final QuizOptionMapper quizOptionMapper;
    private final QuizOptionService quizOptionService;
    private final MessageSource messageSource;
    
    public QuizController(QuizRepository quizRepository, QuizQuestionRepository quizQuestionRepository,
                          QuizOptionRepository quizOptionRepository, QuizQuestionMapper quizQuestionMapper,
                          DtoHelper dtoHelper, QuizQuestionService quizQuestionService,
                          QuizOptionMapper quizOptionMapper, QuizOptionService quizOptionService,
                          MessageSource messageSource) {
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizOptionRepository = quizOptionRepository;
        this.quizQuestionMapper = quizQuestionMapper;
        this.dtoHelper = dtoHelper;
        this.quizQuestionService = quizQuestionService;
        this.quizOptionMapper = quizOptionMapper;
        this.quizOptionService = quizOptionService;
        this.messageSource = messageSource;
    }
    
    @GetMapping("")
    public ResponseEntity<List<QuizQuestionDTO>> getAllQuizQuestions() {
        return ResponseEntity.ok(quizQuestionRepository.findAll().stream().map(dtoHelper::toQuizQuestionDTO).toList());
    }
    
    @PostMapping("/add/option")
    public ResponseEntity<QuizOptionDTO> getAllQuizOptions(@RequestBody QuizOptionDTO quizOptionDTO) {
        
        log.info("[POST] /add/option\nBody:\n{}",quizOptionDTO);
        long quizQuestionId = quizOptionDTO.getQuizQuestionId();
        Optional<QuizQuestion> questionOptional = quizQuestionRepository.findById(quizQuestionId);
        
        if (questionOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.question", null,
                                                                      Locale.getDefault()));
        }
        
        QuizOption quizOption = quizOptionMapper.toQuizOption(quizOptionDTO);
        quizOption.setQuizQuestion(questionOptional.get());
        QuizOption saved = quizOptionService.save(quizOption);
        
        return ResponseEntity.ok(dtoHelper.toQuizOptionDTO(saved));
    }
    
    @PostMapping("/new")
    public ResponseEntity<QuizQuestionDTO> createNewQuizQuestion(@RequestBody QuizQuestionDTO quizQuestionDTO) {
        QuizQuestion quizQuestion = quizQuestionMapper.toQuizQuestion(quizQuestionDTO);
        List<QuizOption> quizOptions = new ArrayList<>();
        
        log.info("Quiz options size in body: {}", quizQuestionDTO.getOptions().size());
        
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
    
    @PostMapping("/delete/{quizOptionId}")
    public ResponseEntity<QuizOptionDTO> deleteQuizOption(@PathVariable long quizOptionId) {
        Optional<QuizOption> optionOptional = quizOptionRepository.findById(quizOptionId);
        
        if (optionOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.option", null, Locale.getDefault()));
        }
        
        return ResponseEntity.ok(dtoHelper.toQuizOptionDTO(quizOptionService.deleteById(quizOptionId)));
    }
    
    @PatchMapping("/option/{optionId}")
    public ResponseEntity<QuizOptionDTO> editQuizOption(@PathVariable long optionId, @RequestBody QuizOptionDTO quizOptionDTO) {
        log.info("[PATCH] /option/{}", optionId);
        
        Optional<QuizOption> optionOptional = quizOptionRepository.findById(optionId);
        
        if (optionOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.option", null, Locale.getDefault()));
        }
        
        QuizOption existingOption = optionOptional.get();
        
        if (quizOptionDTO.getOptionText() != null && !quizOptionDTO.getOptionText().trim().isEmpty()) {
            existingOption.setOptionText(quizOptionDTO.getOptionText());
        }
        
        existingOption.setCorrect(quizOptionDTO.isCorrect());
        QuizOption saved = quizOptionService.save(existingOption);
        
        return ResponseEntity.ok(dtoHelper.toQuizOptionDTO(saved));
    }
    
    @PatchMapping("/question/{questionId}")
    public ResponseEntity<QuizQuestionDTO> editQuizQuestion(@PathVariable long questionId, @RequestBody QuizQuestionDTO quizQuestionDTO) {
        log.info("[PATCH] /question/{}", questionId);
        
        Optional<QuizQuestion> questionOptional = quizQuestionRepository.findById(questionId);
        
        if (questionOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.question", null, Locale.getDefault()));
        }
        
        QuizQuestion existingQuestion = questionOptional.get();
        
        if (quizQuestionDTO.getQuestionText() != null && !quizQuestionDTO.getQuestionText().trim().isEmpty()) {
            existingQuestion.setQuestionText(quizQuestionDTO.getQuestionText());
        }
        
        QuizQuestion saved = quizQuestionService.save(existingQuestion);
        
        return ResponseEntity.ok(dtoHelper.toQuizQuestionDTO(saved));
    }
}