package net.therap.app.helper;

import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import net.therap.app.service.CourseService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

/**
 * @author gazizafor
 * @since 30/7/25
 */
@Component
public class CourseMappingHelper {
    
    private final CourseService courseService;
    
    public CourseMappingHelper(CourseService courseService) {
        this.courseService = courseService;
    }
    
    public Course map(Long courseid) {
        if (courseid == null ||  courseid == 0) {
            return null;
        }
        return courseService.findById(courseid)
                .orElseThrow(() -> new NoSuchElementException("Course not found with ID: " + courseid));
    }
}