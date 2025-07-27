package net.therap.app.service;

import net.therap.app.model.Course;
import net.therap.app.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }
    
    public Course save(Course course) {
        return courseRepository.save(course);
    }
    
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }
}