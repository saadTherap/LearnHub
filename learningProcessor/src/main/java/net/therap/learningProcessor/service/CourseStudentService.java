package net.therap.learningProcessor.service;

import net.therap.learningProcessor.dto.CourseDetailDto;
import net.therap.learningProcessor.dto.StudentContentStatusDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.entity.Student;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
public interface CourseStudentService {

    void enrollInCourse(Long studentId, Long courseId);

    List<Student> getStudentsEnrolledInCourse(Long courseId);

    List<Long> getEnrolledCourseIdsByStudent(Long studentId);

    boolean markContentCompleted(Long studentId, Long contentId);

    void submitAssignment(Long studentId, Long contentId, String downloadUrl);

    List<StudentContentStatusDto> getContentStatusByStudentId(Long studentId);

    public CourseDetailDto getCourseDetailWithProgress(Long studentId, Long courseId);

    public StudentCourseProgressDto getStudentCourseProgress(Long studentId, Long courseId);

    public List<StudentCourseProgressDto> getAllStudentProgressForCourse(Long courseId);
}