package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.cache.support.CacheInvalidationUtil;
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
import net.therap.learningProcessor.mapper.StudentContentCompletionMapper;
import net.therap.learningProcessor.mapper.StudentCourseProgressMapper;
import net.therap.learningProcessor.mapper.StudentMapper;
import net.therap.learningProcessor.repository.CourseEnrollmentRepository;
import net.therap.learningProcessor.repository.StudentContentCompletionRepository;
import net.therap.learningProcessor.repository.StudentRepository;
import net.therap.learningProcessor.repository.StudentSubmissionRepository;
import net.therap.learningProcessor.util.CourseProgressUtil;
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
    private final StudentSubmissionRepository studentSubmissionRepository;
    private final CacheInvalidationUtil cacheInvalidationUtil;

    private final StudentValidator studentValidator;

    private final StudentMapper studentMapper;
    private final StudentCourseProgressMapper studentCourseProgressMapper;
    private final StudentContentCompletionMapper studentContentCompletionMapper;

    @Override
    @Transactional
    public void enrollInCourse(Long studentId, Long courseId) {
        if (courseEnrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ResourceAlreadyExistsException("error.enrollment.alreadyExists", studentId, courseId);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("error.student.notFound", studentId));

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
                courseId,
                CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE,
                CacheConstants.STUDENTS_BY_COURSE
        );
    }

    @Override
    public List<Long> getStudentIdsEnrolledInCourse(Long courseId) {

        return courseEnrollmentRepository.findByCourseId(courseId).stream()
                .map(CourseEnrollment::getStudent)
                .map(Student::getId)
                .toList();
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

        StudentContentCompletion alreadyExists = studentContentCompletionRepository.findByStudentIdAndContentId(studentId, contentId).orElse(null);

        if(alreadyExists != null) {
            return true;
        }

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
    public StudentContentCompletionDto getByStudentIdAndContentId(Long studentId, Long contentId) {
        studentValidator.validateStudentExists(studentId);

        StudentContentCompletion completion = studentContentCompletionRepository.findByStudentIdAndContentId(studentId, contentId).orElse(null);

        return studentContentCompletionMapper.toDto(completion);
    }

    @Override
    public CourseDetailWithProgressDto getCourseDetailWithProgress(Long studentId, CourseDetailWithProgressDto courseDetail) {
        studentValidator.validateStudentExists(studentId);

        List<StudentContentCompletionDto> completedContentDtos = studentContentCompletionRepository.getStudentContentStatusByStudentId(studentId);

        CourseProgressUtil.addProgressDetailsToCourse(courseDetail, completedContentDtos);

        return courseDetail;
    }

    @Override
    public StudentCourseProgressDto getStudentCourseProgress(Long studentId, CourseDetailWithProgressDto courseDetail) {
        studentValidator.validateStudentExists(studentId);

        Student student = studentRepository.findById(studentId).orElseThrow();

        return createStudentCourseProgressDto(student, courseDetail);
    }

    @Override
    public List<StudentCourseProgressDto> getAllStudentProgressForCourse(CourseDetailWithProgressDto courseDetail) {

        List<Student> students = getEnrolledStudents(courseDetail.getId());

        return students.stream()
                .map(student -> createStudentCourseProgressDto(student, courseDetail))
                .toList();
    }

    @Transactional
    @Override
    public void deleteAllEnrollments(Long studentId) {

        studentSubmissionRepository.deleteAllByStudentId(studentId);
        studentContentCompletionRepository.deleteAllByStudentId(studentId);
        courseEnrollmentRepository.deleteAllByStudentId(studentId);
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