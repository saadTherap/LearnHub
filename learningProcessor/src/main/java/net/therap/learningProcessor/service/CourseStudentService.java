package net.therap.learningProcessor.service;

import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.dto.StudentDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
public interface CourseStudentService {

    void enrollInCourse(Long studentId, Long courseId);

    List<Long> getStudentIdsEnrolledInCourse(Long courseId);

    List<StudentDto> getStudentsEnrolledInCourse(Long courseId);

    List<Long> getEnrolledCourseIdsByStudent(Long studentId);

    boolean completeContent(Long studentId, Long contentId);

    List<StudentContentCompletionDto> getContentStatusByStudentId(Long studentId);

    public StudentContentCompletionDto getByStudentIdAndContentId(Long studentId, Long contentId);

    public CourseDetailWithProgressDto getCourseDetailWithProgress(Long studentId, CourseDetailWithProgressDto courseDetailWithProgressDto);

    public StudentCourseProgressDto getStudentCourseProgress(Long studentId, CourseDetailWithProgressDto courseDetailWithProgressDto);

    public List<StudentCourseProgressDto> getAllStudentProgressForCourse(CourseDetailWithProgressDto courseDetailWithProgressDto);

    public void deleteAllEnrollments(Long studentId);
}