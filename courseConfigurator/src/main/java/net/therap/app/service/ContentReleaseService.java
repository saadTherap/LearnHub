package net.therap.app.service;

import net.therap.app.model.ContentRelease;
import net.therap.app.model.Submission;
import net.therap.app.repository.ContentReleaseRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Service
@Transactional(readOnly = true)
public class ContentReleaseService {
    
    private final ContentReleaseRepository contentReleaseRepository;
    private final MessageSource messageSource;
    
    public ContentReleaseService(ContentReleaseRepository contentReleaseRepository, MessageSource messageSource) {
        this.contentReleaseRepository = contentReleaseRepository;
        this.messageSource = messageSource;
    }
    
    @Transactional
    public ContentRelease delete(long id) {
        Optional<ContentRelease> contentReleaseOptional = contentReleaseRepository.findById(id);
        
        if (contentReleaseOptional.isPresent()) {
            contentReleaseOptional.get().setDeleted(true);
            return contentReleaseRepository.save(contentReleaseOptional.get());
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.contentRelease", null, Locale.getDefault()));
    }
    
    @Transactional
    public void save(ContentRelease contentReleaseToPublish) {
        contentReleaseRepository.save(contentReleaseToPublish);
    }
    
    public List<Submission> findSubmissionByInstructorId(long instructorId) {
        List<ContentRelease> contentReleases = contentReleaseRepository.findByInstructorId(instructorId);
        
        return contentReleases.stream().filter(Submission.class::isInstance).map(Submission.class::cast).toList();
    }
}