package net.therap.learningProcessor.service;

import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.dto.StudentDto;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
public interface CourseStudentService {

    void enrollInCourse(Long studentId, Long courseId);

    List<StudentDto> getStudentsEnrolledInCourse(Long courseId);

    List<Long> getEnrolledCourseIdsByStudent(Long studentId);

    boolean completeContent(Long studentId, Long contentId);

    void submitAssignment(Long studentId, Long contentId, String downloadUrl);

    List<StudentContentCompletionDto> getContentStatusByStudentId(Long studentId);

    public CourseDetailWithProgressDto getCourseDetailWithProgress(Long studentId, Long courseId);

    public StudentCourseProgressDto getStudentCourseProgress(Long studentId, Long courseId);

    public List<StudentCourseProgressDto> getAllStudentProgressForCourse(Long courseId);
}