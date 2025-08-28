package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.helper.ContentHelper;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Content;
import net.therap.app.model.ContentRelease;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.model.Quiz;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.repository.ContentReleaseRepository;
import net.therap.app.repository.ContentRepository;
import net.therap.cache.support.CacheInvalidationUtil;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@Service
@Transactional(readOnly = true)
public class ContentService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ContentRepository contentRepository;
    private final DtoHelper dtoHelper;
    private final CacheInvalidationUtil cacheInvalidationUtil;
    private final MessageSource messageSource;
    private final ContentHelper contentHelper;
    private final ContentReleaseService contentReleaseService;
    
    public ContentService(ContentRepository contentRepository, DtoHelper dtoHelper, CacheInvalidationUtil cacheInvalidationUtil, MessageSource messageSource, ContentHelper contentHelper, ContentReleaseService contentReleaseService) {
        this.contentRepository = contentRepository;
        this.dtoHelper = dtoHelper;
        this.cacheInvalidationUtil = cacheInvalidationUtil;
        this.messageSource = messageSource;
        this.contentHelper = contentHelper;
        this.contentReleaseService = contentReleaseService;
    }
    
    public List<Content> findByModuleId(long moduleId) {
        return contentRepository.findContentReleaseByModuleId(moduleId);
    }
    
    public Optional<Content> findContentByContentReleaseId(long contentReleaseId) {
        Optional<Content> contentOptional = contentRepository.findContentByContentReleaseId(contentReleaseId);
        logger.info("Optional isPresent => {}", contentOptional.isPresent());
        contentOptional.ifPresent(content -> Hibernate.initialize(content.getContentReleases()));
        
        return contentOptional;
    }
    
    public Optional<Content> findById(long contentId) {
        return contentRepository.findById(contentId);
    }
    
    public List<ContentRelease> findAllContentReleases() {
        List<Content> contents = contentRepository.findAll();
        
        return contents.stream().map(Content::getCurrentContentRelease).collect(Collectors.toList());
    }
    
    public List<ContentRelease> findAllReleases(long contentReleaseId) throws NoSuchElementException {
        Optional<Content> contentOptional = contentRepository.findByContentReleaseIdWithReleases(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        
        Hibernate.initialize(contentOptional.get().getContentReleases());
        logger.info("ContentReleases: {}", contentOptional.get().getContentReleases().size());
        
        return contentOptional.get().getContentReleases();
    }
    
    public List<ContentRelease> findAllReleasesOfContent(long contentId) {
        Optional<Content> contentOptional = contentRepository.findById(contentId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        Hibernate.initialize(contentOptional.get().getContentReleases());
        
        return contentOptional.get().getContentReleases();
    }
    
    public ContentRelease findSpecificContentRelease(long contentReleaseId, long releaseNum) throws NoSuchElementException {
        Optional<Content> contentOptional = findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        
        for (ContentRelease contentRelease : contentOptional.get().getContentReleases()) {
            if (contentRelease.getRelease() == releaseNum) {
                return contentRelease;
            }
        }
        
        throw new NoSuchElementException();
    }
    
    public boolean isPublishable(Content content) {
        return contentHelper.isValidForPublication(content.getCurrentContentRelease());
    }
    
    @Transactional(readOnly = true)
    public ContentCatalogueDTO toDetailedContentCatalogueDTO(ContentRelease contentRelease) {
        ContentCatalogueDTO dto = dtoHelper.toContentCatalogueDTO(contentRelease);
        
        return switch (dto.getType()) {
            case "LECTURE" -> dtoHelper.populateLectureCatalogDTO(dto, contentRelease);
            case "QUIZ" -> dtoHelper.populateQuizCatalogDTO(dto, contentRelease);
            case "SUBMISSION" -> dtoHelper.populateSubmissionCatalogDTO(dto, contentRelease);
            default -> null;
        };
    }
    
    @Transactional
    public Content save(Content content) {
        Content saved = contentRepository.save(content);

        Long moduleId = saved.getModule().getId();
        Long courseId = saved.getModule().getCourse().getId();

        cacheInvalidationUtil.invalidateCachesAfterCommit(
                moduleId,
                CacheConstants.MODULES
        );
        cacheInvalidationUtil.invalidateCachesAfterCommit(
                courseId,
                CacheConstants.MODULES_BY_COURSE,
                CacheConstants.COURSES,
                CacheConstants.COURSE_CATALOG,
                CacheConstants.COURSE_CATALOG_PUBLIC
        );

        if (saved.getId() > 0) {
            cacheInvalidationUtil.invalidateCachesAfterCommit(
                    saved.getId(),
                    CacheConstants.CONTENT_CATALOG,
                    CacheConstants.CONTENT_RELEASE_LIST
            );

            if (saved.getCurrentContentRelease() != null) {
                String key = saved.getId() + ":" + saved.getCurrentContentRelease().getRelease();
                cacheInvalidationUtil.invalidateCachesAfterCommit(
                        key,
                        CacheConstants.CONTENT_RELEASES
                );
            }
        }

        return saved;
    }
    
    @Transactional
    public Content deleteById(long id) {
        logger.info("Content id deletion: {}", id);

        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        messageSource.getMessage("not.found.content", null, Locale.getDefault())
                ));

        long contentId = content.getId();
        Long moduleId = content.getModule().getId();
        Long courseId = content.getModule().getCourse().getId();

        Long releaseNum = null;
        if (content.getCurrentContentRelease() != null) {
            releaseNum = content.getCurrentContentRelease().getRelease();
        }

        content.setDeleted(true);
        content.setCurrentContentRelease(null);
        Content saved = contentRepository.save(content);

        if (contentId > 0) {
            cacheInvalidationUtil.invalidateCachesAfterCommit(
                    contentId,
                    CacheConstants.CONTENT_CATALOG,
                    CacheConstants.CONTENT_RELEASE_LIST
            );
        }
        if (releaseNum != null && releaseNum > 0) {
            String releaseKey = contentId + ":" + releaseNum;
            cacheInvalidationUtil.invalidateCachesAfterCommit(
                    releaseKey,
                    CacheConstants.CONTENT_RELEASES
            );
        }

        cacheInvalidationUtil.invalidateCachesAfterCommit(
                moduleId,
                CacheConstants.MODULES
        );
        cacheInvalidationUtil.invalidateCachesAfterCommit(
                courseId,
                CacheConstants.MODULES_BY_COURSE,
                CacheConstants.COURSES,
                CacheConstants.COURSE_CATALOG,
                CacheConstants.COURSE_CATALOG_PUBLIC
        );

        return saved;
    }
    
    @Transactional(readOnly = true)
    public void publishContentsOfCourse(Course course) {
        for (Module module : course.getModules()) {
            for (Content content : module.getContents()) {
                ContentRelease contentRelease = content.getCurrentContentRelease();
                if (contentRelease.getRelease() == ReleaseStatus.DRAFT.getReleaseNumber()) {
                    contentRelease.setRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
                    contentReleaseService.save(contentRelease);
                }
            }
        }
    }
}