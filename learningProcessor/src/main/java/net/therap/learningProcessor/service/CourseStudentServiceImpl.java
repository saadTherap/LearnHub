package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.CourseEnrollment;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.entity.StudentContentCompletion;
import net.therap.learningProcessor.entity.StudentSubmission;
import net.therap.learningProcessor.mapper.StudentCourseProgressMapper;
import net.therap.learningProcessor.mapper.StudentMapper;
import net.therap.learningProcessor.repository.CourseEnrollmentRepository;
import net.therap.learningProcessor.repository.StudentContentCompletionRepository;
import net.therap.learningProcessor.repository.StudentRepository;
import net.therap.learningProcessor.repository.StudentSubmissionRepository;
import net.therap.learningProcessor.util.CourseProgressUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Service
@RequiredArgsConstructor
public class CourseStudentServiceImpl implements CourseStudentService {

    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final StudentContentCompletionRepository studentContentCompletionRepository;
    private final StudentSubmissionRepository studentSubmissionRepository;

    private final CourseClient courseClient;

    private final StudentMapper studentMapper;
    private final StudentCourseProgressMapper studentCourseProgressMapper;

    @Override
    public void enrollInCourse(Long studentId, Long courseId) {
        if (courseEnrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            return;
        }

        Student student = studentRepository.findById(studentId).orElseThrow();

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourseId(courseId);

        courseEnrollmentRepository.save(enrollment);
    }

    @Override
    public List<StudentDto> getStudentsEnrolledInCourse(Long courseId) {
        return courseEnrollmentRepository.findByCourseId(courseId).stream()
                .map(CourseEnrollment::getStudent)
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    public List<Long> getEnrolledCourseIdsByStudent(Long studentId) {
        return courseEnrollmentRepository.findByStudentId(studentId).stream()
                .map(CourseEnrollment::getCourseId)
                .toList();
    }

    @Override
    public boolean completeContent(Long studentId, Long contentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();

        StudentContentCompletion completion = new StudentContentCompletion();
        completion.setContentId(contentId);
        completion.setStudent(student);

        studentContentCompletionRepository.save(completion);

        return true;
    }

    @Override
    public void submitAssignment(Long studentId, Long contentId, String downloadUrl) {
        StudentSubmission submission = studentSubmissionRepository.findByStudentIdAndContentId(studentId, contentId)
                .orElseGet(StudentSubmission::new);

        Student student = studentRepository.findById(studentId).orElseThrow();

        submission.setStudent(student);
        submission.setContentId(contentId);
        submission.setDownloadUrl(downloadUrl);
        submission.setSubmittedAt(LocalDateTime.now());

        studentSubmissionRepository.save(submission);
    }

    @Override
    public List<StudentContentCompletionDto> getContentStatusByStudentId(Long studentId) {
        return studentContentCompletionRepository.getStudentContentStatusByStudentId(studentId);
    }

    @Override
    public CourseDetailWithProgressDto getCourseDetailWithProgress(Long studentId, Long courseId) {
        CourseDetailWithProgressDto courseDetail = courseClient.getCourseDetail(courseId);
        List<StudentContentCompletionDto> completedContentDtos = studentContentCompletionRepository.getStudentContentStatusByStudentId(studentId);

        CourseProgressUtil.addProgressDetailsToCourse(courseDetail, completedContentDtos);

        return courseDetail;
    }

    @Override
    public StudentCourseProgressDto getStudentCourseProgress(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        CourseDetailWithProgressDto courseDetail = courseClient.getCourseDetail(courseId);

        return createStudentCourseProgressDto(student, courseDetail);
    }

    @Override
    public List<StudentCourseProgressDto> getAllStudentProgressForCourse(Long courseId) {
        CourseDetailWithProgressDto courseDetail = courseClient.getCourseDetail(courseId);
        List<Student> students = getEnrolledStudents(courseId);

        return students.stream()
                .map(student -> createStudentCourseProgressDto(student, courseDetail))
                .toList();
    }

    private List<Student> getEnrolledStudents(Long courseId) {
        return courseEnrollmentRepository.findByCourseId(courseId).stream()
                .map(CourseEnrollment::getStudent)
                .toList();
    }

    private StudentCourseProgressDto createStudentCourseProgressDto(Student student, CourseDetailWithProgressDto courseDetail) {

        List<StudentContentCompletionDto> completedContentDtos = studentContentCompletionRepository.getStudentContentStatusByStudentId(student.getId());

        double progress = CourseProgressUtil.calculateCourseProgress(completedContentDtos, courseDetail.getModules());

        return studentCourseProgressMapper.toDto(student, courseDetail, progress);
    }
}