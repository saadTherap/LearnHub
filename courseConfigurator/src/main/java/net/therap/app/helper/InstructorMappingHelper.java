package net.therap.app.helper;

import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import net.therap.app.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

/**
 * @author gazizafor
 * @since 30/7/25
 */
@Component
public class InstructorMappingHelper {
    
    private InstructorService instructorService;
    
    @Autowired
    public InstructorMappingHelper(InstructorService instructorService) {
        this.instructorService = instructorService;
    }
    
    public Instructor map(Long instructorId) {
        if (instructorId == null ||  instructorId == 0) {
            return null;
        }
        return instructorService.getInstructorById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + instructorId));
    }
}