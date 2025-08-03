package net.therap.app.controller;

import net.therap.app.dto.*;
import net.therap.app.helper.ContentHelper;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.LectureMapper;
import net.therap.app.mapper.SubmissionMapper;
import net.therap.app.model.*;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.repository.ContentReleaseRepository;
import net.therap.app.repository.ContentRepository;
import net.therap.app.service.*;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static net.therap.app.util.StringUtil.isEmpty;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@RestController
@RequestMapping("/api/contents")
public class ContentController {
    
    private final Logger logger = LoggerFactory.getLogger(ContentController.class);
    
    private final ContentService contentService;
    
    private final DtoHelper dtoHelper;
    
    private final ContentHelper contentHelper;
    private final LectureService lectureService;
    private final QuizService quizService;
    private final SubmissionService submissionService;
    private final ContentReleaseRepository contentReleaseRepository;
    private final CourseService courseService;
    private final ContentRepository contentRepository;
    private final LectureMapper lectureMapper;
    private final SubmissionMapper submissionMapper;
    
    @Autowired
    public ContentController(ContentService contentService, DtoHelper dtoHelper, ContentHelper contentHelper, ContentRepository contentRepository, LectureService lectureService, QuizService quizService, SubmissionService submissionService, ContentReleaseRepository contentReleaseRepository, CourseService courseService, LectureMapper lectureMapper, SubmissionMapper submissionMapper) {
        this.contentService = contentService;
        this.dtoHelper = dtoHelper;
        this.contentHelper = contentHelper;
        this.lectureService = lectureService;
        this.quizService = quizService;
        this.submissionService = submissionService;
        this.contentReleaseRepository = contentReleaseRepository;
        this.courseService = courseService;
        this.contentRepository = contentRepository;
        this.lectureMapper = lectureMapper;
        this.submissionMapper = submissionMapper;
    }
    
    @GetMapping("/byModule/{moduleId}")
    public List<ContentCatalogueDTO> getContentByModuleId(@PathVariable long moduleId) {
        List<Content> contents = contentService.findByModuleId(moduleId);
        
        return contents.stream().map(dtoHelper::toContentCatalogueDTO).toList();
    }
    
    @GetMapping("/detail/{contentReleaseId}")
    public ResponseEntity<ContentCatalogueDTO> getContentReleaseById(@PathVariable long contentReleaseId) {
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        return contentOptional.map(
                content -> new ResponseEntity<>(dtoHelper.toDetailedContentCatalogueDTO(content.getCurrentContentRelease()), HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<ContentReleaseDTO>> getAllContentReleases() {
        logger.info("Fetching all content releases");
        List<ContentRelease> contentReleases = contentService.findAllContents();
        
        logger.info("content releases: {}", contentReleases);
        return new ResponseEntity<>(contentReleases.stream()
                                            .map(dtoHelper::toContentReleaseDTO).toList(),HttpStatus.OK);
    }
    
    @GetMapping("/{contentReleaseId}/releases")
    public ResponseEntity<List<ContentReleaseDTO>> getAllContentReleases(@PathVariable long contentReleaseId) {
        List<ContentRelease> contentReleases = contentService.findAllReleases(contentReleaseId);
        
        return new ResponseEntity<>(contentReleases.stream().map(dtoHelper::toContentReleaseDTO).toList(), HttpStatus.OK);
    }
    
    @GetMapping("/{contentReleaseId}/releases/{releaseNum}")
    public ResponseEntity<ContentReleaseDTO> getSpecificContentRelease(@PathVariable long contentReleaseId, @PathVariable long releaseNum) {
        return ResponseEntity.ok(dtoHelper.toContentReleaseDTO(contentService.findSpecificContentRelease(contentReleaseId, releaseNum)));
    }
    
    @PostMapping("/draft")
    public ResponseEntity<ContentReleaseDTO> createContent(@RequestBody @Validated(OnCreate.class) ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        Content content = contentHelper.getContent(contentCatalogueDTO);
        ContentRelease contentRelease = contentHelper.getContentRelease(contentCatalogueDTO, content);
        
        if (isNull(contentRelease)) {
            throw new BadRequestException();
        }
        
        content.setCurrentContentRelease(contentRelease);
        contentService.save(content);

        if (contentRelease instanceof Lecture) {
            lectureService.save((Lecture) contentRelease);

        } else if (contentRelease instanceof Quiz) {
            quizService.save((Quiz) contentRelease);

        } else {
            submissionService.save((Submission) contentRelease);
        }
        
        return new ResponseEntity<>(dtoHelper.toContentReleaseDTO(contentRelease), HttpStatus.CREATED);
    }
    
    @PatchMapping("/publish/{contentReleaseId}")
    public ResponseEntity<ContentReleaseDTO> publishContentRelease(@PathVariable long contentReleaseId, @RequestBody @Validated(OnUpdate.class) ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ContentRelease contentRelease = contentOptional.get().getCurrentContentRelease();
        Course course = contentRelease.getContent().getModule().getCourse();
        ContentRelease contentReleaseToPublish = cloneContentRelease(contentRelease, contentCatalogueDTO);
        
        if (contentHelper.isValidForPublication(contentRelease, contentReleaseToPublish)) {
            if (course.getCurrentRelease() == ReleaseStatus.DRAFT.getReleaseNumber()) {
                logger.info("Publishing a COURSE from draft version: {}", course.getId());
                contentReleaseToPublish.setRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
                course.setCurrentRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
                contentReleaseRepository.save(contentReleaseToPublish);
                
                List<ContentRelease> contentReleases = contentOptional.get().getContentReleases();
                contentReleases.add(contentReleaseToPublish);
                contentOptional.get().setContentReleases(contentReleases);
                contentRepository.save(contentOptional.get());
                
                courseService.save(course);
                
            } else {
                logger.info("Publishing a new version of COURSE id:{} from prev version: {}", course.getId(), course.getCurrentRelease());
                logger.info("Content version: {}", contentRelease.getRelease());
                
                if (contentRelease.getRelease() != ReleaseStatus.DRAFT.getReleaseNumber()) {
                    contentReleaseToPublish.setId(0);
                }
                
                contentReleaseToPublish.setRelease(course.getCurrentRelease() + 1);
                course.setCurrentRelease(course.getCurrentRelease() + 1);
                
                contentReleaseRepository.save(contentReleaseToPublish);
                
                contentOptional.get().setCurrentContentRelease(contentReleaseToPublish);
                List<ContentRelease> contentReleases = contentOptional.get().getContentReleases();
                contentReleases.add(contentReleaseToPublish);
                contentOptional.get().setContentReleases(contentReleases);
                contentRepository.save(contentOptional.get());
                
                courseService.save(course);
            }
            
            return ResponseEntity.ok(dtoHelper.toContentReleaseDTO(contentReleaseToPublish));
        }
        
        return ResponseEntity.badRequest().build();
    }
    
    private ContentRelease cloneContentRelease(ContentRelease contentRelease, ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        ContentRelease contentReleaseCopied;
        
        if (contentRelease instanceof Lecture) {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("LECTURE")) {
                throw new BadRequestException();
            }
            
            contentReleaseCopied = new Lecture();
            BeanUtils.copyProperties(contentRelease, contentReleaseCopied);
            lectureMapper.updateLectureFromLectureCatalogDto((LectureCatalogDTO) contentCatalogueDTO, (Lecture) contentReleaseCopied);
            
        } else if (contentRelease instanceof Quiz) {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("QUIZ")) {
                throw new BadRequestException();
            }
            
            contentReleaseCopied = new Quiz();
            
        } else {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("SUBMISSION")) {
                throw new BadRequestException();
            }
            
            contentReleaseCopied = new Submission();
            BeanUtils.copyProperties(contentRelease, contentReleaseCopied);
            submissionMapper.updateSubmissionFromSubmissionCatalogDto((SubmissionCatalogueDTO) contentCatalogueDTO, (Submission) contentReleaseCopied);
        }
        
        return contentReleaseCopied;
    }
}