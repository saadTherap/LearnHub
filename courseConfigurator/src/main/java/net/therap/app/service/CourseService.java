package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
@Transactional(readOnly = true)
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private HazelcastCacheService hazelcastCacheService;
    
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course save(Course course) {
        Course savedCourse = courseRepository.save(course);

        invalidateCachesAfterCommit(savedCourse.getId(), CacheConstants.COURSES, CacheConstants.COURSE_CATALOG);

        return savedCourse;
    }

    @Transactional
    public void deleteById(Long id) {
        courseRepository.deleteById(id);

        invalidateCachesAfterCommit(id, CacheConstants.COURSES, CacheConstants.COURSE_CATALOG);
    }

    /**
     * Invalidate multiple caches after the current transaction commits successfully.
     *
     * @param id The cache key to remove.
     * @param mapNames One or more cache map names to remove the key from.
     */
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