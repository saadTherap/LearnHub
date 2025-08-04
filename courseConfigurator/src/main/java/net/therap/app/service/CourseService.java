package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.repository.CourseRepository;
import net.therap.app.util.CacheInvalidationUtil;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
@Transactional(readOnly = true)
public class CourseService {
    

    private final CacheInvalidationUtil cacheInvalidationUtil;
    private final CourseRepository courseRepository;
    private final ModuleService moduleService;
    private final HazelcastCacheService hazelcastCacheService;
    private final MessageSource messageSource;
    
    public CourseService(CacheInvalidationUtil cacheInvalidationUtil, CourseRepository courseRepository, ModuleService moduleService, HazelcastCacheService hazelcastCacheService, MessageSource messageSource) {
        this.cacheInvalidationUtil = cacheInvalidationUtil;
        this.courseRepository = courseRepository;
        this.moduleService = moduleService;
        this.hazelcastCacheService = hazelcastCacheService;
        this.messageSource = messageSource;
    }
    
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course save(Course course) {
        Course savedCourse = courseRepository.save(course);

        cacheInvalidationUtil.invalidateCacheAfterCommit(String.valueOf(savedCourse.getId()), CacheConstants.COURSES, CacheConstants.COURSE_CATALOG);

        return savedCourse;
    }

    @Transactional
    public Course deleteById(Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        
        if (courseOptional.isPresent()) {
            courseOptional.get().setDeleted(true);
            Course deletedCourse = courseRepository.save(courseOptional.get());
            invalidateCachesAfterCommit(id, CacheConstants.COURSES, CacheConstants.COURSE_CATALOG);
            return deletedCourse;
        }

        cacheInvalidationUtil.invalidateCacheAfterCommit(String.valueOf(id), CacheConstants.COURSES, CacheConstants.COURSE_CATALOG);
        throw new NoSuchElementException(messageSource.getMessage("not.found.course", null, Locale.getDefault()));
    }
    
    private void invalidateCachesAfterCommit(Long id, String... mapNames) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (String mapName : mapNames) {
                    hazelcastCacheService.remove(mapName, id);
                }
            }
        });
    }

    public boolean isPublishable(Course course) {
        for (Module module : course.getModules()) {
            if (moduleService.isPublishable(module)) {
                return true;
            }
        }
        
        return false;
    }
}