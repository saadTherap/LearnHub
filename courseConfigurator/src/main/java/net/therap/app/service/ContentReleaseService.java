package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.model.ContentRelease;
import net.therap.app.model.Instructor;
import net.therap.app.model.Submission;
import net.therap.app.repository.ContentReleaseRepository;
import net.therap.app.repository.InstructorRepository;
import net.therap.cache.support.CacheInvalidationUtil;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Service
@Transactional(readOnly = true)
public class ContentReleaseService {
    
    private final ContentReleaseRepository contentReleaseRepository;
    private final MessageSource messageSource;
    private final InstructorRepository instructorRepository;
    private final CacheInvalidationUtil cacheInvalidationUtil;
    
    public ContentReleaseService(ContentReleaseRepository contentReleaseRepository, MessageSource messageSource, InstructorRepository instructorRepository, CacheInvalidationUtil cacheInvalidationUtil) {
        this.contentReleaseRepository = contentReleaseRepository;
        this.messageSource = messageSource;
        this.instructorRepository = instructorRepository;
        this.cacheInvalidationUtil = cacheInvalidationUtil;
    }
    
    public Optional<ContentRelease> findById(long id) {
        return contentReleaseRepository.findById(id);
    }
    
    @Transactional
    public ContentRelease delete(long id) {
        Optional<ContentRelease> contentReleaseOptional = contentReleaseRepository.findById(id);

        if (contentReleaseOptional.isPresent()) {
            ContentRelease contentRelease = contentReleaseOptional.get();
            contentRelease.setDeleted(true);
            ContentRelease deletedContentRelease = contentReleaseRepository.save(contentRelease);

            cacheInvalidationUtil.invalidateCachesAfterCommit(
                    id,
                    CacheConstants.CONTENT_CATALOG,
                    CacheConstants.CONTENT_RELEASE_LIST
            );

            cacheInvalidationUtil.invalidateCachesByPrefixAfterCommit(
                    String.valueOf(id),
                    CacheConstants.CONTENT_RELEASES
            );

            return deletedContentRelease;
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.contentRelease", null, Locale.getDefault()));
    }

    @Transactional
    public void save(ContentRelease contentReleaseToPublish) {
        ContentRelease savedContentRelease = contentReleaseRepository.save(contentReleaseToPublish);
        long courseId = contentReleaseToPublish.getContent().getModule().getCourse().getId();

        cacheInvalidationUtil.invalidateCachesAfterCommit(
                savedContentRelease.getId(),
                CacheConstants.CONTENT_CATALOG,
                CacheConstants.CONTENT_RELEASE_LIST
        );
        
        cacheInvalidationUtil.invalidateCachesAfterCommit(
                courseId,
                CacheConstants.COURSES,
                CacheConstants.COURSE_CATALOG,
                CacheConstants.COURSE_CATALOG_PUBLIC
        );

        cacheInvalidationUtil.invalidateCachesByPrefixAfterCommit(
                String.valueOf(savedContentRelease.getId()),
                CacheConstants.CONTENT_RELEASES
        );
    }
    
    public List<Submission> findSubmissionByInstructorId(long instructorId) {
        List<ContentRelease> contentReleases = contentReleaseRepository.findByInstructorId(instructorId);
        
        return contentReleases.stream().filter(Submission.class::isInstance).map(Submission.class::cast).toList();
    }
    
    public List<ContentRelease> findAllDrafts(long instructorId) {
        Optional<Instructor> instructorOptional = instructorRepository.findById(instructorId);
        
        if (instructorOptional.isPresent()) {
            Instructor instructor = instructorOptional.get();
            List<ContentRelease> contentReleases = contentReleaseRepository.findAll();
            List<ContentRelease> filteredContentReleases = new ArrayList<>();
            
            for (ContentRelease contentRelease : contentReleases) {
                long ownerId = contentRelease.getContent().getModule().getCourse().getInstructor().getId();
                
                if (instructorId == ownerId) {
                    filteredContentReleases.add(contentRelease);
                }
            }
            
            return filteredContentReleases;
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.instructor", null, Locale.getDefault()));
    }
}