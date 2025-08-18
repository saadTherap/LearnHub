package net.therap.app.controller;

import lombok.extern.slf4j.Slf4j;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.*;
import net.therap.app.helper.ContentHelper;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.LectureMapper;
import net.therap.app.mapper.SubmissionMapper;
import net.therap.app.model.*;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.service.*;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import net.therap.cache.support.HazelcastCacheService;
import org.apache.coyote.BadRequestException;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.Objects.isNull;
import static net.therap.app.util.StringUtil.isEmpty;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@RestController
@RequestMapping("/contents")
@Slf4j
public class ContentController {
    
    private final ContentService contentService;
    private final DtoHelper dtoHelper;
    private final ContentHelper contentHelper;
    private final LectureService lectureService;
    private final QuizService quizService;
    private final SubmissionService submissionService;
    private final ContentReleaseService contentReleaseService;
    private final CourseService courseService;
    private final LectureMapper lectureMapper;
    private final SubmissionMapper submissionMapper;
    private final MessageSource messageSource;
    private final HazelcastCacheService hazelcastCacheService;

    
    @Autowired
    public ContentController(ContentService contentService, DtoHelper dtoHelper, ContentHelper contentHelper,
                             LectureService lectureService, QuizService quizService,
                             SubmissionService submissionService, ContentReleaseService contentReleaseService,
                             CourseService courseService, LectureMapper lectureMapper,
                             SubmissionMapper submissionMapper, MessageSource messageSource, HazelcastCacheService hazelcastCacheService) {
        this.contentService = contentService;
        this.dtoHelper = dtoHelper;
        this.contentHelper = contentHelper;
        this.lectureService = lectureService;
        this.quizService = quizService;
        this.submissionService = submissionService;
        this.contentReleaseService = contentReleaseService;
        this.courseService = courseService;
        this.lectureMapper = lectureMapper;
        this.submissionMapper = submissionMapper;
        this.messageSource = messageSource;
        this.hazelcastCacheService = hazelcastCacheService;
    }
    
    @GetMapping("/byModule/{moduleId}")
    public List<ContentCatalogueDTO> getContentByModuleId(@PathVariable long moduleId) {
        log.info("[GET] /contents/byModule/{}", moduleId);
        List<Content> contents = contentService.findByModuleId(moduleId);
        
        return contents.stream().map(dtoHelper::toContentCatalogueDTO).toList();
    }

    @GetMapping("/detail/{contentReleaseId}")
    public ResponseEntity<ContentCatalogueDTO> getContentReleaseById(@PathVariable long contentReleaseId) {
        log.info("[GET] /contents/detail/{}", contentReleaseId);
        ContentCatalogueDTO cached = hazelcastCacheService.get(CacheConstants.CONTENT_CATALOG, contentReleaseId);
        
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);

        return contentOptional
                .map(content -> {
                    ContentRelease contentRelease = content.getCurrentContentRelease();
                    
                    if (contentRelease instanceof Quiz quiz) {
                        Hibernate.initialize(quiz.getQuestions());
                    }
                    
                    ContentCatalogueDTO dto = contentService.toDetailedContentCatalogueDTO(contentRelease);

                    hazelcastCacheService.put(CacheConstants.CONTENT_CATALOG, contentReleaseId, dto);

                    return new ResponseEntity<>(dto, HttpStatus.OK);
                })
                .orElseThrow(() -> new NoSuchElementException(messageSource.getMessage("content.not.found", null, Locale.getDefault())));
    }
    
    @GetMapping("/detail/exact/{contentReleaseId}")
    public ResponseEntity<ContentReleaseDTO> getContentReleaseDetailedInstructorView(@PathVariable long contentReleaseId) {
        log.info("[GET] /contents/detail/exact/{}", contentReleaseId);
        
        Optional<ContentRelease> contentReleaseOptional = contentReleaseService.findById(contentReleaseId);
        
        return contentReleaseOptional
                .map(contentRelease -> {
                    ContentReleaseDTO dto = dtoHelper.toContentReleaseDTO(contentRelease);
                    
                    return new ResponseEntity<>(dto, HttpStatus.OK);
                })
                .orElseThrow(() -> new NoSuchElementException(messageSource.getMessage("content.not.found", null, Locale.getDefault())));
    }


    @GetMapping
    public ResponseEntity<List<ContentReleaseDTO>> getAllContentReleases() {
        log.info("[GET] /contents");
        List<ContentRelease> contentReleases = contentService.findAllContents();
        
        return new ResponseEntity<>(contentReleases.stream().map(dtoHelper::toContentReleaseDTO).toList(),
                                    HttpStatus.OK);
    }

    @GetMapping("/{contentReleaseId}/releases")
    public ResponseEntity<List<ContentReleaseDTO>> getAllContentReleases(@PathVariable long contentReleaseId) {
        log.info("[GET] /contents/{}/releases", contentReleaseId);
        List<ContentReleaseDTO> cached = hazelcastCacheService.get(CacheConstants.CONTENT_RELEASE_LIST, contentReleaseId);
        
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        List<ContentRelease> contentReleases = contentService.findAllReleases(contentReleaseId);
        List<ContentReleaseDTO> dtos = contentReleases.stream()
                .map(dtoHelper::toContentReleaseDTO)
                .toList();
        hazelcastCacheService.put(CacheConstants.CONTENT_RELEASE_LIST, contentReleaseId, dtos);
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/releases/{contentId}")
    public ResponseEntity<List<ContentReleaseDTO>> getContentReleases(@PathVariable long contentId) {
        log.info("[GET] /contents/releases/{}", contentId);
        List<ContentRelease> releases = contentService.findAllReleasesOfContent(contentId);
        
        return ResponseEntity.ok(releases.stream().map(dtoHelper::toContentReleaseDTO).toList());
    }

    @GetMapping("/{contentReleaseId}/releases/{releaseNum}")
    public ResponseEntity<ContentReleaseDTO> getSpecificContentRelease(@PathVariable long contentReleaseId,
                                                                       @PathVariable long releaseNum) {
        log.info("[GET] /contents/{}/releases/{}", contentReleaseId, releaseNum);
        String key = contentReleaseId + ":" + releaseNum;
        ContentReleaseDTO cached = hazelcastCacheService.get(CacheConstants.CONTENT_RELEASES, key);
        
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        ContentRelease contentRelease = contentService.findSpecificContentRelease(contentReleaseId, releaseNum);
        ContentReleaseDTO dto = dtoHelper.toContentReleaseDTO(contentRelease);
        hazelcastCacheService.put(CacheConstants.CONTENT_RELEASES, key, dto);

        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/draft")
    public ResponseEntity<List<ContentReleaseDTO>> getDraftContentReleases() {
        log.info("[GET] /contents/draft");
        List<ContentRelease> contentReleases = contentReleaseService.findAllDrafts(1);
        
        return ResponseEntity.ok(contentReleases.stream().map(dtoHelper::toContentReleaseDTO).toList());
    }
    
    @PostMapping("/draft")
    public ResponseEntity<ContentReleaseDTO> createContent(@RequestBody @Validated(OnCreate.class) ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        log.info("[POST] /contents/draft\nRequest Body:\n{}", contentCatalogueDTO);
        log.info("Creating draft course...");
        Content content = contentHelper.getContent(contentCatalogueDTO);
        ContentRelease contentRelease = contentHelper.getContentRelease(contentCatalogueDTO, content);
        
        if (isNull(contentRelease)) {
            throw new IllegalArgumentException();
        }
        
        Content savedContent = contentService.save(content);
        ContentRelease savedContentRelease;
        
        if (contentRelease instanceof Lecture) {
            savedContentRelease = lectureService.save((Lecture) contentRelease);
            
        } else if (contentRelease instanceof Quiz) {
            savedContentRelease = quizService.save((Quiz) contentRelease);
            
        } else {
            savedContentRelease = submissionService.save((Submission) contentRelease);
        }
        
        savedContent.setCurrentContentRelease(savedContentRelease);
        savedContent.getContentReleases().add(savedContentRelease);
        
        contentService.save(savedContent);
        
        return new ResponseEntity<>(dtoHelper.toContentReleaseDTO(savedContentRelease), HttpStatus.CREATED);
    }
    
    @PatchMapping("/publish/{contentReleaseId}")
    public ResponseEntity<ContentReleaseDTO> publishContentRelease(@PathVariable long contentReleaseId,
                                                                   @RequestBody @Validated(OnUpdate.class) ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        
        log.info("[PATCH] /contents/publish/{}\nRequest Body:\n{}", contentReleaseId, contentCatalogueDTO);
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        Content content = contentOptional.get();
        ContentRelease oldContentRelease = content.getCurrentContentRelease();
        Course course = oldContentRelease.getContent().getModule().getCourse();
        
        ContentRelease newContentRelease = createNewContentRelease(oldContentRelease, contentCatalogueDTO);
        
        if (contentHelper.isValidForPublication(oldContentRelease, newContentRelease)) {
            if (course.getCurrentRelease() == ReleaseStatus.DRAFT.getReleaseNumber()) {
                log.info("Publishing a COURSE from draft version: courseId => {}", course.getId());
                newContentRelease.setRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
                course.setCurrentRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
                
            } else {
                log.info("Publishing a new version of COURSE id:{} from prev version: {}", course.getId(),
                            course.getCurrentRelease());
                log.info("Content version: {}", oldContentRelease.getRelease());
                
                newContentRelease.setId(0L);
                
                newContentRelease.setRelease(course.getCurrentRelease() + 1);
                course.setCurrentRelease(course.getCurrentRelease() + 1);
            }
            
            newContentRelease.setContent(content);
            content.setCurrentContentRelease(newContentRelease);
            content.getContentReleases().add(newContentRelease);
            
            contentService.save(content);
            courseService.save(course);
            
            return ResponseEntity.ok(dtoHelper.toContentReleaseDTO(newContentRelease));
        }
        
        throw new BadRequestException(messageSource.getMessage("bad.request.publish.content",null, Locale.getDefault()));
    }
    
    @PatchMapping("/draft/{contentReleaseId}")
    public ResponseEntity<ContentReleaseDTO> editDraftContentRelease(@PathVariable long contentReleaseId,
                                                                   @RequestBody @Validated(OnUpdate.class) ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        
        log.info("[PATCH] /contents/draft/{}\nRequest Body:\n{}", contentReleaseId, contentCatalogueDTO);
        log.info("Editing draft contentRelease...");
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        Content content = contentOptional.get();
        ContentRelease oldContentRelease = content.getCurrentContentRelease();
        
        if (oldContentRelease.getRelease() !=  ReleaseStatus.DRAFT.getReleaseNumber()) {
            throw new BadRequestException(messageSource.getMessage("bad.request.edit.draft",null, Locale.getDefault()));
        }
        ContentRelease newContentRelease = updateContentRelease(oldContentRelease, contentCatalogueDTO);
        contentReleaseService.save(newContentRelease);
        
        return ResponseEntity.ok(dtoHelper.toContentReleaseDTO(newContentRelease));
    }
    
    @PatchMapping("/edit/{contentReleaseId}")
    public ResponseEntity<ContentCatalogueDTO> editContentMetadata(@PathVariable long contentReleaseId,
                                                                   @RequestBody @Validated(OnUpdate.class) ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        log.info("[PATCH] /edit/{}", contentReleaseId);
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        Content content = contentOptional.get();
        
        if (content.getTitle().equals(contentCatalogueDTO.getTitle())) {
            throw new BadRequestException(messageSource.getMessage("bad.request.content.unchanged", null, Locale.getDefault()));
        }
        
        content.setTitle(contentCatalogueDTO.getTitle());
        Content updatedContent = contentService.save(content);
        
        return new ResponseEntity<>(dtoHelper.toContentCatalogueDTO(updatedContent), HttpStatus.OK);
    }
    
    @PostMapping("/delete/{contentReleaseId}")
    public ResponseEntity<ContentReleaseDTO> deleteContentRelease(@PathVariable long contentReleaseId) {
        log.info("[DELETE] /delete/{}", contentReleaseId);
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        ContentRelease contentRelease = contentOptional.get().getCurrentContentRelease();
        
        if (contentRelease.getRelease() == ReleaseStatus.DRAFT.getReleaseNumber()) {
            contentReleaseService.delete(contentRelease.getId());
            contentService.deleteById(contentOptional.get().getId());
            
        } else {
            contentReleaseService.delete(contentRelease.getId());
            contentService.deleteById(contentOptional.get().getId());
        }
        
        return ResponseEntity.ok(dtoHelper.toContentReleaseDTO(contentRelease));
    }
    
    private ContentRelease createNewContentRelease(ContentRelease original,
                                                   ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        
        ContentRelease newContentRelease;
        
        if (original instanceof Lecture) {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("LECTURE")) {
                throw new BadRequestException();
            }
            
            
            newContentRelease = new Lecture();
            BeanUtils.copyProperties(original, newContentRelease, "id");
            lectureMapper.updateLectureFromLectureCatalogDto((LectureCatalogDTO) contentCatalogueDTO, (Lecture) newContentRelease);
            
        } else if (original instanceof Quiz) {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("QUIZ")) {
                throw new BadRequestException();
            }
            
            Quiz originalQuiz = (Quiz) original;
            Quiz newQuiz = new Quiz();
            BeanUtils.copyProperties(originalQuiz, newQuiz, "id", "questions");

// Cast the DTO to access quiz-specific properties
            QuizCatalogDTO quizCatalogDTO = (QuizCatalogDTO) contentCatalogueDTO;

// Create a new list for the new quiz's questions
            List<QuizQuestion> newQuestions = new ArrayList<>();

// Add new questions from the DTO first
            for (QuizQuestionDTO originalQuestionDTO : quizCatalogDTO.getQuestions()) {
                QuizQuestion newQuestion = new QuizQuestion();
                newQuestion.setQuestionText(originalQuestionDTO.getQuestionText());
                newQuestion.setId(0L); // Ensure new ID
                newQuestion.setQuiz(newQuiz);
                
                // Now, copy the options for the new question from the DTO
                List<QuizOption> newOptions = new ArrayList<>();
                for (QuizOptionDTO originalOptionDTO : originalQuestionDTO.getOptions()) {
                    QuizOption newOption = new QuizOption();
                    newOption.setOptionText(originalOptionDTO.getOptionText());
                    newOption.setCorrect(originalOptionDTO.isCorrect());
                    newOption.setId(0L); // Ensure new ID
                    newOption.setQuizQuestion(newQuestion);
                    newOptions.add(newOption);
                }
                newQuestion.setOptions(newOptions);
                newQuestions.add(newQuestion);
            }
            
            log.info("{} quiz questions created", newQuestions.size());

// Set the complete list of questions on the new quiz
            newQuiz.setQuestions(newQuestions);
            
            newContentRelease = newQuiz;
            newContentRelease.setId(0L);
            
        } else {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("SUBMISSION")) {
                throw new BadRequestException();
            }
            
            newContentRelease = new Submission();
            BeanUtils.copyProperties(original, newContentRelease, "id");
            submissionMapper.updateSubmissionFromSubmissionCatalogDto((SubmissionCatalogueDTO) contentCatalogueDTO, (Submission) newContentRelease);
        }
        
        newContentRelease.setId(0L);
        
        return newContentRelease;
    }
    
    private ContentRelease updateContentRelease(ContentRelease contentRelease,
                                                ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        
        if (contentRelease instanceof Lecture) {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("LECTURE")) {
                throw new BadRequestException(messageSource.getMessage("conflict.type", null, Locale.getDefault()));
            }
            
            lectureMapper.updateLectureFromLectureCatalogDto((LectureCatalogDTO) contentCatalogueDTO, (Lecture) contentRelease);
            
        } else if (contentRelease instanceof Quiz) {
            throw new BadRequestException(messageSource.getMessage("conflict.type", null, Locale.getDefault()));
            
        } else {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("SUBMISSION")) {
                throw new BadRequestException(messageSource.getMessage("conflict.type", null, Locale.getDefault()));
            }
            
            submissionMapper.updateSubmissionFromSubmissionCatalogDto((SubmissionCatalogueDTO) contentCatalogueDTO, (Submission) contentRelease);
        }
        
        return contentRelease;
    }
}