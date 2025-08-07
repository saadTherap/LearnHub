package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Content;
import net.therap.app.model.ContentRelease;
import net.therap.app.model.Quiz;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.repository.ContentRepository;
import net.therap.app.util.CacheInvalidationUtil;
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
    
    public ContentService(ContentRepository contentRepository, DtoHelper dtoHelper, CacheInvalidationUtil cacheInvalidationUtil, MessageSource messageSource) {
        this.contentRepository = contentRepository;
        this.dtoHelper = dtoHelper;
        this.cacheInvalidationUtil = cacheInvalidationUtil;
        this.messageSource = messageSource;
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
    
    public List<ContentRelease> findAllContents() {
        List<Content> contents = contentRepository.findAll();
        
        return contents.stream().map(Content::getCurrentContentRelease).collect(Collectors.toList());
    }
    
    public List<ContentRelease> findAllReleases(long contentReleaseId) throws NoSuchElementException {
        Optional<Content> contentOptional = findContentByContentReleaseId(contentReleaseId);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        
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
        return content.getCurrentContentRelease().getRelease() != ReleaseStatus.DRAFT.getReleaseNumber();
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
        if (content.getCurrentContentRelease() != null) {
            String key = content.getId() + ":" + content.getCurrentContentRelease().getRelease();
            cacheInvalidationUtil.invalidateCacheAfterCommit(String.valueOf(content.getId()), CacheConstants.CONTENT_CATALOG, CacheConstants.CONTENT_RELEASE_LIST);
            cacheInvalidationUtil.invalidateCacheAfterCommit(key, CacheConstants.CONTENT_RELEASES);
        }

        return contentRepository.save(content);
    }
    
    @Transactional
    public Content deleteById(long id) {
        logger.info("Content id deletion: {}",id);
        Optional<Content> contentOptional = contentRepository.findById(id);
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        long releaseNum = contentOptional.get().getCurrentContentRelease().getRelease();
        
        Content content = contentOptional.get();
        content.setDeleted(true);
        contentOptional.get().setCurrentContentRelease(null);
        Content deletedContent = contentRepository.save(content);
        
        String key = content.getId() + ":" + releaseNum;
        cacheInvalidationUtil.invalidateCacheAfterCommit(String.valueOf(content.getId()), CacheConstants.CONTENT_CATALOG, CacheConstants.CONTENT_RELEASE_LIST);
        cacheInvalidationUtil.invalidateCacheAfterCommit(key, CacheConstants.CONTENT_RELEASES);
        
        return deletedContent;
    }
    
    public void loadQuestions(Quiz previousQuiz) {
        Hibernate.initialize(previousQuiz.getQuestions());
    }
}