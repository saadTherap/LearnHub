package net.therap.learningProcessor.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.auth.lib.context.UserRequestCache;
import net.therap.auth.lib.util.AuthDataUtil;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.exception.ForbiddenException;
import net.therap.learningProcessor.exception.ResourceNotFoundException;
import net.therap.learningProcessor.exception.UnauthorizedException;
import net.therap.learningProcessor.repository.CourseEnrollmentRepository;
import net.therap.learningProcessor.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * @author avidewan
 * @since 8/13/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    @Override
    public void authorize(AccessLevel level, HttpServletRequest request) {
        authorize(level, Map.of(), request);
    }

    @Override
    public void authorize(AccessLevel level, Map<String, Object> params, HttpServletRequest request) {
        if (level == AccessLevel.PUBLIC) {
            return;
        }

        UserRequestCache.UserInfo userInfo = getCurrentUserInfo(request);

        log.info("userinfo: {}", userInfo);

        if (userInfo == null) {
            throwUnauthorized("error.auth.required");
        }

        if(isAdmin(userInfo)) {
            return;
        }

        switch (level) {

            case TEACHER_ONLY -> checkTeacher(userInfo);

            case STUDENT_WITH_ID -> checkStudentWithId(userInfo, params);

            case TEACHER_AND_STUDENT_WITH_ID -> checkTeacherOrStudentWithId(userInfo, params);

            case STUDENT_ENROLLED_IN_COURSE -> checkStudentEnrolledInCourse(userInfo, params);

            default -> throwForbidden("error.access.content.denied");
        }
    }

    // ---------- PRIVATE STRATEGY METHODS ----------

    private UserRequestCache.UserInfo getCurrentUserInfo(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        log.info("User Id: {}", userId);

        if (userId == null) {
            throwUnauthorized("error.auth.required");
        }

        return AuthDataUtil.getUserInfo(userId);
    }

    private boolean isTeacher(UserRequestCache.UserInfo userInfo) {
        return "INSTRUCTOR".equals(userInfo.role());
    }

    private boolean isStudent(UserRequestCache.UserInfo userInfo) {
        return "STUDENT".equals(userInfo.role());
    }

    private boolean isAdmin(UserRequestCache.UserInfo userInfo) {
        return "ADMIN".equals(userInfo.role());
    }

    private void checkTeacher(UserRequestCache.UserInfo userInfo) {
        if (!isTeacher(userInfo)) {
            throwForbidden("error.access.teacher");
        }
    }

    private void checkStudentWithId(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        Long studentId = (Long) params.get("studentId");

        if (studentId == null) {
            throwForbidden("error.access.student");
        }

        Student student = studentRepository.findByEmail(userInfo.email());

        if(student == null) {
            throw new ResourceNotFoundException("error.student.notFound", userInfo.email());
        }

        log.info("Email of the student from token: {}", student.getId());
        log.info("Student Id trying to access: {}", studentId);

        if (!(isStudent(userInfo) && student.getId() == studentId)) {
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

    private void checkTeacherOrStudentWithId(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        Long studentId = (Long) params.get("studentId");

        if (isTeacher(userInfo)) {
            return; // Teacher always allowed
        }
        checkStudentWithId(userInfo, Map.of("studentId", studentId));
    }

    private void throwUnauthorized(String messageKey) {
        throw new UnauthorizedException(messageKey);
    }

    private void throwForbidden(String messageKey) {
        throw new ForbiddenException(messageKey);
    }
}