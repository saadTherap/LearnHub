package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.cache.support.CacheInvalidationUtil;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.CourseEnrollment;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.entity.StudentContentCompletion;
import net.therap.learningProcessor.exception.ResourceAlreadyExistsException;
import net.therap.learningProcessor.exception.ResourceNotFoundException;
import net.therap.learningProcessor.mapper.StudentCourseProgressMapper;
import net.therap.learningProcessor.mapper.StudentMapper;
import net.therap.learningProcessor.repository.CourseEnrollmentRepository;
import net.therap.learningProcessor.repository.StudentContentCompletionRepository;
import net.therap.learningProcessor.repository.StudentRepository;
import net.therap.learningProcessor.util.CourseProgressUtil;
import net.therap.learningProcessor.validator.CourseValidator;
import net.therap.learningProcessor.validator.StudentValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseStudentServiceImpl implements CourseStudentService {

    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final StudentContentCompletionRepository studentContentCompletionRepository;
    private final CacheInvalidationUtil cacheInvalidationUtil;

    private final CourseClient courseClient;

    private final CourseValidator courseValidator;
    private final StudentValidator studentValidator;

    private final StudentMapper studentMapper;
    private final StudentCourseProgressMapper studentCourseProgressMapper;

    @Override
    @Transactional
    public void enrollInCourse(Long studentId, Long courseId) {
        if (courseEnrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ResourceAlreadyExistsException("error.enrollment.alreadyExists", studentId, courseId);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("error.student.notFound", studentId));

        courseValidator.validateCourseExists(courseId);

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourseId(courseId);
        courseEnrollmentRepository.save(enrollment);

        String scKey = studentId + ":" + courseId;
        cacheInvalidationUtil.invalidateCachesAfterCommit(
                scKey,
                CacheConstants.COURSE_PROGRESS_DETAIL,
                CacheConstants.STUDENT_COURSE_PROGRESS
        );

        cacheInvalidationUtil.invalidateCachesAfterCommit(
                String.valueOf(courseId),
                CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE,
                CacheConstants.STUDENTS_BY_COURSE
        );
    }

    @Override
    public List<Long> getStudentIdsEnrolledInCourse(Long courseId) {
        courseValidator.validateCourseExists(courseId);

        return courseEnrollmentRepository.findByCourseId(courseId).stream()
                .map(CourseEnrollment::getStudent)
                .map(Student::getId)
                .toList();
    }

    @Override
    public List<StudentDto> getStudentsEnrolledInCourse(Long courseId) {
        courseValidator.validateCourseExists(courseId);

        return courseEnrollmentRepository.findByCourseId(courseId).stream()
                .map(CourseEnrollment::getStudent)
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    public List<Long> getEnrolledCourseIdsByStudent(Long studentId) {
        studentValidator.validateStudentExists(studentId);

        return courseEnrollmentRepository.findByStudentId(studentId).stream()
                .map(CourseEnrollment::getCourseId)
                .toList();
    }

    @Override
    @Transactional
    public boolean completeContent(Long studentId, Long contentId) {
        Student student = studentRepository.findById(studentId).
                orElseThrow(() -> new ResourceNotFoundException("error.student.notFound", studentId));

        courseValidator.validateContentExists(contentId);

        StudentContentCompletion completion = new StudentContentCompletion();
        completion.setContentId(contentId);
        completion.setStudent(student);

        studentContentCompletionRepository.save(completion);

        return true;
    }

    @Override
    public List<StudentContentCompletionDto> getContentStatusByStudentId(Long studentId) {
        studentValidator.validateStudentExists(studentId);

        return studentContentCompletionRepository.getStudentContentStatusByStudentId(studentId);
    }

    @Override
    public CourseDetailWithProgressDto getCourseDetailWithProgress(Long studentId, Long courseId) {
        studentValidator.validateStudentExists(studentId);
        courseValidator.validateCourseExists(courseId);

        CourseDetailWithProgressDto courseDetail = courseClient.getCourseDetail(courseId);
        List<StudentContentCompletionDto> completedContentDtos = studentContentCompletionRepository.getStudentContentStatusByStudentId(studentId);

        CourseProgressUtil.addProgressDetailsToCourse(courseDetail, completedContentDtos);

        return courseDetail;
    }

    @Override
    public StudentCourseProgressDto getStudentCourseProgress(Long studentId, Long courseId) {
        studentValidator.validateStudentExists(studentId);
        courseValidator.validateCourseExists(courseId);

        Student student = studentRepository.findById(studentId).orElseThrow();
        CourseDetailWithProgressDto courseDetail = courseClient.getCourseDetail(courseId);

        return createStudentCourseProgressDto(student, courseDetail);
    }

    @Override
    public List<StudentCourseProgressDto> getAllStudentProgressForCourse(Long courseId) {
        courseValidator.validateCourseExists(courseId);

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