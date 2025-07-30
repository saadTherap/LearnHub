package net.therap.app.service;

import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }
    
    @Transactional
    public Course save(Course course) {
        return courseRepository.save(course);
    }
    
    @Transactional
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
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