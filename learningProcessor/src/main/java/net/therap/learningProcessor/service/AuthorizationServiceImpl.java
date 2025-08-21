package net.therap.learningProcessor.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.lib.context.UserRequestCache;
import net.therap.auth.lib.util.AuthDataUtil;
import net.therap.learningProcessor.entity.Course;
import net.therap.learningProcessor.entity.Instructor;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.exception.ForbiddenException;
import net.therap.learningProcessor.exception.ResourceNotFoundException;
import net.therap.learningProcessor.exception.UnauthorizedException;
import net.therap.learningProcessor.repository.CourseEnrollmentRepository;
import net.therap.learningProcessor.repository.CourseRepository;
import net.therap.learningProcessor.repository.InstructorRepository;
import net.therap.learningProcessor.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    @Override
    public void authorize(AccessLevel level, HttpServletRequest request) {
        authorize(level, Map.of(), request);
    }

    @Override
    public void authorize(AccessLevel level, Map<String, Object> params, HttpServletRequest request) {

        if (level == AccessLevel.PUBLIC) return; // always allow public access

        UserRequestCache.UserInfo userInfo = getCurrentUserInfo(request);

        if (isAdmin(userInfo)) return; // admins bypass all checks

        switch (level) {

            case INSTRUCTOR_ONLY -> checkInstructor(userInfo);

            case STUDENT_ONLY -> checkStudent(userInfo);

            case STUDENT_WITH_ID -> checkStudentWithId(userInfo, params);

            case STUDENT_ENROLLED_IN_COURSE -> checkStudentEnrolledInCourse(userInfo, params);

            case INSTRUCTOR_OF_COURSE -> checkInstructorOfCourse(userInfo, params);

            case INSTRUCTOR_OR_STUDENT_WITH_ID -> checkInstructorOrStudentWithId(userInfo, params);

            case INSTRUCTOR_OR_STUDENT_ENROLLED_IN_COURSE -> checkInstructorOrStudentEnrolled(userInfo, params);

            case INSTRUCTOR_OF_COURSE_OR_STUDENT_WITH_ID -> checkInstructorOfCourseOrStudentWithId(userInfo, params);

            default -> throwForbidden("error.access.content.denied");
        }
    }

    // ---------- PRIVATE HELPERS ----------

    private UserRequestCache.UserInfo getCurrentUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            throwUnauthorized("error.auth.required");
        }

        return AuthDataUtil.getUserInfo(userId);
    }

    private boolean isAdmin(UserRequestCache.UserInfo userInfo) {
        return "ADMIN".equals(userInfo.role());
    }

    private boolean isInstructor(UserRequestCache.UserInfo userInfo) {
        return "INSTRUCTOR".equals(userInfo.role());
    }

    private boolean isStudent(UserRequestCache.UserInfo userInfo) {
        return "STUDENT".equals(userInfo.role());
    }

    private void checkInstructor(UserRequestCache.UserInfo userInfo) {
        if (!isInstructor(userInfo)) {
            throwForbidden("error.access.instructor");
        }
    }

    private void checkStudent(UserRequestCache.UserInfo userInfo) {
        if (!isStudent(userInfo)) {
            throwForbidden("error.access.student");
        }
    }

    private void checkStudentWithId(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        Long studentId = (Long) params.get("studentId");

        if (studentId == null) {
            throwForbidden("error.access.student");
        }

        Student student = studentRepository.findByEmail(userInfo.email())
                .orElseThrow(() -> new ResourceNotFoundException("error.student.notFound", userInfo.email()));

        if (!isStudent(userInfo) || !(student.getId() == (studentId))) {
            throwForbidden("error.access.student.mismatch");
        }
    }

    private void checkStudentEnrolledInCourse(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        Long studentId = (Long) params.get("studentId");
        Long courseId = (Long) params.get("courseId");

        if (studentId == null || courseId == null) {
            throwForbidden("error.access.enrollment.denied");
        }

        checkStudentWithId(userInfo, Map.of("studentId", studentId));

        if (!courseEnrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throwForbidden("error.access.not.enrolled");
        }
    }

    private void checkInstructorOfCourse(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        Long courseId = (Long) params.get("courseId");

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("error.course.notFound", courseId));

        Instructor instructor = instructorRepository.findByEmail(userInfo.email())
                .orElseThrow(() -> new ResourceNotFoundException("error.instructor.notFound.byEmail", userInfo.email()));

        if (!instructor.getId().equals(course.getInstructorId())) {
            throwForbidden("error.access.instructor.mismatch");
        }
    }

    private void checkInstructorOrStudentWithId(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        if (isInstructor(userInfo)) return;

        checkStudentWithId(userInfo, params);
    }

    private void checkInstructorOrStudentEnrolled(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        if (isInstructor(userInfo)) return;

        checkStudentEnrolledInCourse(userInfo, params);
    }

    private void checkInstructorOfCourseOrStudentWithId(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        if (isInstructor(userInfo)) {
            checkInstructorOfCourse(userInfo, params);

        } else {
            checkStudentWithId(userInfo, params);
        }
    }

    private void throwUnauthorized(String messageKey) {
        throw new UnauthorizedException(messageKey);
    }

    private void throwForbidden(String messageKey) {
        throw new ForbiddenException(messageKey);
    }
}
