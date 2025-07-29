package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.dto.CourseDetailDto;
import net.therap.learningProcessor.dto.StudentContentStatusDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.entity.CourseEnrollment;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.entity.StudentContent;
import net.therap.learningProcessor.entity.StudentSubmission;
import net.therap.learningProcessor.eum.CompletionStatus;
import net.therap.learningProcessor.mapper.StudentCourseProgressMapper;
import net.therap.learningProcessor.repository.CourseEnrollmentRepository;
import net.therap.learningProcessor.repository.StudentContentRepository;
import net.therap.learningProcessor.repository.StudentRepository;
import net.therap.learningProcessor.repository.StudentSubmissionRepository;
import net.therap.learningProcessor.util.CourseProgressUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Service
@RequiredArgsConstructor
public class CourseStudentServiceImpl implements CourseStudentService {

    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final StudentContentRepository studentContentRepository;
    private final StudentSubmissionRepository studentSubmissionRepository;

    private final CourseClient courseClient;

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
    public List<Student> getStudentsEnrolledInCourse(Long courseId) {
        return courseEnrollmentRepository.findByCourseId(courseId).stream()
                .map(CourseEnrollment::getStudent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getEnrolledCourseIdsByStudent(Long studentId) {
        return courseEnrollmentRepository.findByStudentId(studentId).stream()
                .map(CourseEnrollment::getCourseId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean markContentCompleted(Long studentId, Long contentId) {
        StudentContent studentContent = studentContentRepository.findByStudentIdAndContentId(studentId, contentId)
                .orElse(null);

        if(Objects.isNull(studentContent)) {
            return false;
        }

        studentContent.setStatus(CompletionStatus.COMPLETED);
        studentContentRepository.save(studentContent);

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
    public List<StudentContentStatusDto> getContentStatusByStudentId(Long studentId) {
        return studentContentRepository.getStudentContentStatusByStudentId(studentId);
    }

    @Override
    public CourseDetailDto getCourseDetailWithProgress(Long studentId, Long courseId) {
        CourseDetailDto courseDetail = courseClient.getCourseDetail(courseId);
        Map<Long, CompletionStatus> contentStatusMap = getContentStatusMap(studentId);

        CourseProgressUtil.enrichCourseDetailWithProgress(courseDetail, contentStatusMap);

        return courseDetail;
    }

    @Override
    public StudentCourseProgressDto getStudentCourseProgress(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        CourseDetailDto courseDetail = courseClient.getCourseDetail(courseId);

        return createStudentCourseProgressDto(student, courseDetail);
    }

    @Override
    public List<StudentCourseProgressDto> getAllStudentProgressForCourse(Long courseId) {
        CourseDetailDto courseDetail = courseClient.getCourseDetail(courseId);
        List<Student> students = getStudentsEnrolledInCourse(courseId);

        return students.stream()
                .map(student -> createStudentCourseProgressDto(student, courseDetail))
                .collect(Collectors.toList());
    }

    private Map<Long, CompletionStatus> getContentStatusMap(Long studentId) {
        List<StudentContentStatusDto> contentStatuses =
                studentContentRepository.getStudentContentStatusByStudentId(studentId);

        return contentStatuses.stream()
                .collect(Collectors.toMap(StudentContentStatusDto::getContentId, StudentContentStatusDto::getStatus));
    }

    private StudentCourseProgressDto createStudentCourseProgressDto(Student student, CourseDetailDto courseDetail) {
        Map<Long, CompletionStatus> statusMap = getContentStatusMap(student.getId());
        double progress = CourseProgressUtil.calculateCourseProgress(statusMap, courseDetail.getModules());

        return studentCourseProgressMapper.toDto(student, courseDetail, progress);
    }
}