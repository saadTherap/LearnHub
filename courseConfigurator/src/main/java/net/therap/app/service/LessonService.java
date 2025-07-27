package net.therap.app.service;

import net.therap.app.model.ContentRelease;
import net.therap.app.repository.ContentReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
public class LessonService {
    
    @Autowired
    private ContentReleaseRepository contentReleaseRepository;
    
    public List<ContentRelease> findAll() {
        return contentReleaseRepository.findAll();
    }
    
    public Optional<ContentRelease> findById(long id) {
        return contentReleaseRepository.findById(id);
    }
    
    public ContentRelease save(ContentRelease contentRelease) {
        return contentReleaseRepository.save(contentRelease);
    }
    
    public void deleteById(long id) {
        contentReleaseRepository.deleteById(id);
    }
}