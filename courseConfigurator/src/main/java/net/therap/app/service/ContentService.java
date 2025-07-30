package net.therap.app.service;

import net.therap.app.model.Content;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@Service
public class ContentService {
    
    @Autowired
    private ContentRepository contentRepository;
    
    public List<Content> findByModuleId(long moduleId) {
        return contentRepository.findContentReleaseByModuleId(moduleId);
    }
    
    public Optional<Content> findContentByContentReleaseId(long contentReleaseId) {
        return contentRepository.findContentByContentReleaseId(contentReleaseId);
    }
    
    public boolean isPublishable(Content content) {
        return content.getCurrentContentRelease().getRelease() != ReleaseStatus.DRAFT.getReleaseNumber();
    }
}