package net.therap.learningProcessor.validator;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.service.StudentService;
import org.springframework.stereotype.Component;

/**
 * @author avidewan
 * @since 8/3/25
 */
@Component
@RequiredArgsConstructor
public class StudentValidator {

    private final StudentService studentService;

    public void validateStudentExists(Long studentId) {
        studentService.getStudentById(studentId);
    }
}