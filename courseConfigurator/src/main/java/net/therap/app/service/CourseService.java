package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.repository.CourseRepository;
import net.therap.app.util.CacheInvalidationUtil;
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
    private CacheInvalidationUtil cacheInvalidationUtil;
    
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
    public void deleteById(Long id) {
        courseRepository.deleteById(id);

        cacheInvalidationUtil.invalidateCacheAfterCommit(String.valueOf(id), CacheConstants.COURSES, CacheConstants.COURSE_CATALOG);
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