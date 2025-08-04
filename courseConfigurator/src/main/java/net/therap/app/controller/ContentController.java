package net.therap.app.controller;

import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.dto.ContentReleaseDTO;
import net.therap.app.dto.LectureCatalogDTO;
import net.therap.app.dto.SubmissionCatalogueDTO;
import net.therap.app.helper.ContentHelper;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.LectureMapper;
import net.therap.app.mapper.SubmissionMapper;
import net.therap.app.model.*;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.service.*;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
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
        List<Content> contents = contentService.findByModuleId(moduleId);
        
        return contents.stream().map(dtoHelper::toContentCatalogueDTO).toList();
    }

    @GetMapping("/detail/{contentReleaseId}")
    public ResponseEntity<ContentCatalogueDTO> getContentReleaseById(@PathVariable long contentReleaseId) {

        ContentCatalogueDTO cached = hazelcastCacheService.get(CacheConstants.CONTENT_CATALOG, contentReleaseId);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);

        return contentOptional
                .map(content -> {
                    ContentRelease contentRelease = content.getCurrentContentRelease();
                    ContentCatalogueDTO dto = contentService.toDetailedContentCatalogueDTO(contentRelease);

                    hazelcastCacheService.put(CacheConstants.CONTENT_CATALOG, contentReleaseId, dto);

                    return new ResponseEntity<>(dto, HttpStatus.OK);
                })
                .orElseThrow(() -> new NoSuchElementException(messageSource.getMessage("content.not.found", null, Locale.getDefault())));
    }


    @GetMapping
    public ResponseEntity<List<ContentReleaseDTO>> getAllContentReleases() {
        logger.info("Fetching all content releases");
        List<ContentRelease> contentReleases = contentService.findAllContents();
        
        return new ResponseEntity<>(contentReleases.stream().map(dtoHelper::toContentReleaseDTO).toList(),
                                    HttpStatus.OK);
    }

    @GetMapping("/{contentReleaseId}/releases")
    public ResponseEntity<List<ContentReleaseDTO>> getAllContentReleases(@PathVariable long contentReleaseId) {
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

    @GetMapping("/{contentReleaseId}/releases/{releaseNum}")
    public ResponseEntity<ContentReleaseDTO> getSpecificContentRelease(@PathVariable long contentReleaseId,
                                                                       @PathVariable long releaseNum) {
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
    public ResponseEntity<ContentReleaseDTO> publishContentRelease(@PathVariable long contentReleaseId,
                                                                   @RequestBody @Validated(OnUpdate.class) ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        ContentRelease contentRelease = contentOptional.get().getCurrentContentRelease();
        Course course = contentRelease.getContent().getModule().getCourse();
        ContentRelease contentReleaseToPublish = cloneContentRelease(contentRelease, contentCatalogueDTO);
        
        if (contentHelper.isValidForPublication(contentRelease, contentReleaseToPublish)) {
            if (course.getCurrentRelease() == ReleaseStatus.DRAFT.getReleaseNumber()) {
                logger.info("Publishing a COURSE from draft version: {}", course.getId());
                contentReleaseToPublish.setRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
                course.setCurrentRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
                contentReleaseService.save(contentReleaseToPublish);
                
                List<ContentRelease> contentReleases = contentOptional.get().getContentReleases();
                contentReleases.add(contentReleaseToPublish);
                contentOptional.get().setContentReleases(contentReleases);
                contentService.save(contentOptional.get());
                
                courseService.save(course);
                
            } else {
                logger.info("Publishing a new version of COURSE id:{} from prev version: {}", course.getId(),
                            course.getCurrentRelease());
                logger.info("Content version: {}", contentRelease.getRelease());
                
                if (contentRelease.getRelease() != ReleaseStatus.DRAFT.getReleaseNumber()) {
                    contentReleaseToPublish.setId(0);
                }
                
                contentReleaseToPublish.setRelease(course.getCurrentRelease() + 1);
                course.setCurrentRelease(course.getCurrentRelease() + 1);
                
                contentReleaseService.save(contentReleaseToPublish);
                
                contentOptional.get().setCurrentContentRelease(contentReleaseToPublish);
                List<ContentRelease> contentReleases = contentOptional.get().getContentReleases();
                contentReleases.add(contentReleaseToPublish);
                contentOptional.get().setContentReleases(contentReleases);
                contentService.save(contentOptional.get());
                
                courseService.save(course);
            }
            
            return ResponseEntity.ok(dtoHelper.toContentReleaseDTO(contentReleaseToPublish));
        }
        
        throw new BadRequestException(messageSource.getMessage("bad.request.publish.content",null, Locale.getDefault()));
    }
    
    @PatchMapping("/edit/{contentReleaseId}")
    public ResponseEntity<ContentCatalogueDTO> editContentMetadata(@PathVariable long contentReleaseId,
                                                                   @RequestBody @Validated(OnUpdate.class) ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
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
        Optional<Content> contentOptional = contentService.findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        ContentRelease contentRelease = contentOptional.get().getCurrentContentRelease();
        
        if (contentRelease.getRelease() == ReleaseStatus.DRAFT.getReleaseNumber()) {
            contentReleaseService.delete(contentRelease.getId());
            contentOptional.get().setCurrentContentRelease(null);
            contentService.deleteById(contentOptional.get().getId());
            
        } else {
            contentReleaseService.delete(contentRelease.getId());
            contentOptional.get().setCurrentContentRelease(null);
            contentService.deleteById(contentOptional.get().getId());
        }
        
        return ResponseEntity.ok(dtoHelper.toContentReleaseDTO(contentRelease));
    }
    
    private ContentRelease cloneContentRelease(ContentRelease contentRelease,
                                               ContentCatalogueDTO contentCatalogueDTO) throws BadRequestException {
        ContentRelease contentReleaseCopied;
        
        if (contentRelease instanceof Lecture) {
            if (!isEmpty(contentCatalogueDTO.getType()) && !contentCatalogueDTO.getType().equals("LECTURE")) {
                throw new BadRequestException();
            }
            
            contentReleaseCopied = new Lecture();
            BeanUtils.copyProperties(contentRelease, contentReleaseCopied);
            lectureMapper.updateLectureFromLectureCatalogDto((LectureCatalogDTO) contentCatalogueDTO,
                                                             (Lecture) contentReleaseCopied);
            
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
            submissionMapper.updateSubmissionFromSubmissionCatalogDto((SubmissionCatalogueDTO) contentCatalogueDTO,
                                                                      (Submission) contentReleaseCopied);
        }
        
        return contentReleaseCopied;
    }
}