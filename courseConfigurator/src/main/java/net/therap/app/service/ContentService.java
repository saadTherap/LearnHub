package net.therap.app.service;

import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Content;
import net.therap.app.model.ContentRelease;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.repository.ContentReleaseRepository;
import net.therap.app.repository.ContentRepository;
import org.hibernate.Hibernate;
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
    
    private final ContentRepository contentRepository;
    private final DtoHelper dtoHelper;
    
    public ContentService(ContentRepository contentRepository, DtoHelper dtoHelper) {
        this.contentRepository = contentRepository;
        this.dtoHelper = dtoHelper;
    }
    
    public List<Content> findByModuleId(long moduleId) {
        return contentRepository.findContentReleaseByModuleId(moduleId);
    }
    
    public Optional<Content> findContentByContentReleaseId(long contentReleaseId) {
        Optional<Content> contentOptional = contentRepository.findContentByContentReleaseId(contentReleaseId);
        
        contentOptional.ifPresent(content -> Hibernate.initialize(content.getContentReleases()));
        
        return contentOptional;
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
        return contentRepository.save(content);
    }
    
    @Transactional
    public void delete(Content content) {
        content.setDeleted(true);
        contentRepository.save(content);
    }
}