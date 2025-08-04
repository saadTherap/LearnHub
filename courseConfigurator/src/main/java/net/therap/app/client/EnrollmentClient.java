package net.therap.app.client;

import net.therap.app.dto.StudentDTO;
import net.therap.app.dto.StudentProgressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@FeignClient(name = "{enrollment.client.name}", url = "{enrollment.client.url}")
public interface EnrollmentClient {

    @GetMapping("/enrollments/course/{courseId}")
    public List<StudentDTO> getAllStudentsOfCourseDetailed(@PathVariable long courseId);
    
    @GetMapping("/enrollments/course/{courseId}/studentIds")
    public List<Long> getAllStudentsOfCourse(@PathVariable long courseId);
    
    @GetMapping("/progress/{studentId}/{courseId}")
    public List<StudentProgressDTO> getProgressByStudentAndCourse(@PathVariable long studentId, @PathVariable long courseId);
    
    @GetMapping("/progress/course/{courseId}")
    public List<StudentProgressDTO> getProgressByCourse(@PathVariable long courseId);
}