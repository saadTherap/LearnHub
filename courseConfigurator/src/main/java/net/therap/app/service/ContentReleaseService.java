package net.therap.app.service;

import net.therap.app.model.ContentRelease;
import net.therap.app.repository.ContentReleaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Service
@Transactional(readOnly = true)
public class ContentReleaseService {
    
    private final ContentReleaseRepository contentReleaseRepository;
    
    public ContentReleaseService(ContentReleaseRepository contentReleaseRepository) {
        this.contentReleaseRepository = contentReleaseRepository;
    }
    
    @Transactional
    public void delete(ContentRelease contentRelease) {
        contentRelease.setDeleted(true);
        contentReleaseRepository.save(contentRelease);
    }
    
    @Transactional
    public void save(ContentRelease contentReleaseToPublish) {
        contentReleaseRepository.save(contentReleaseToPublish);
    }
}