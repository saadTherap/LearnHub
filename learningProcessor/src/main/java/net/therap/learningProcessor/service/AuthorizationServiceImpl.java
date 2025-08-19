package net.therap.learningProcessor.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.therap.auth.lib.context.UserRequestCache;
import net.therap.auth.lib.util.AuthDataUtil;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.exception.ForbiddenException;
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
@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    @Override
    public void authorize(AccessLevel level, Map<String, Object> params) {
        if (level == AccessLevel.PUBLIC) {
            return;
        }

        UserRequestCache.UserInfo userInfo = getCurrentUserInfo();

        if (userInfo == null) {
            throwUnauthorized("Authentication required");
        }

        if(isAdmin(userInfo) | isTeacher(userInfo)) {
            return;
        }

        switch (level) {

            case STUDENT_WITH_ID -> checkStudentWithId(userInfo, params);

            case STUDENT_ENROLLED_IN_COURSE -> checkStudentEnrolledInCourse(userInfo, params);

            default -> throwForbidden("Unknown access policy");
        }
    }

    // ---------- PRIVATE STRATEGY METHODS ----------

    private UserRequestCache.UserInfo getCurrentUserInfo() {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        Long userId = (Long) request.getAttribute("userId");

        return AuthDataUtil.getUserInfo(userId);
    }

    private boolean isTeacher(UserRequestCache.UserInfo userInfo) {
        return "TEACHER".equals(userInfo.role());
    }

    private boolean isStudent(UserRequestCache.UserInfo userInfo) {
        return "STUDENT".equals(userInfo.role());
    }

    private boolean isAdmin(UserRequestCache.UserInfo userInfo) {
        return "ADMIN".equals(userInfo.role());
    }

    private void checkStudentWithId(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        Long studentId = (Long) params.get("studentId");
        String email = userInfo.email();

        Student student = studentRepository.findByEmail(email);

        if (!(isStudent(userInfo) && student.getId() == studentId)) {
            throwForbidden("Access denied - student ID mismatch");
        }
    }

    private void checkStudentEnrolledInCourse(UserRequestCache.UserInfo userInfo, Map<String, Object> params) {
        Long studentId = (Long) params.get("studentId");
        Long courseId = (Long) params.get("courseId");

        checkStudentWithId(userInfo, Map.of("studentId", studentId));

        if (!courseEnrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throwForbidden("Access denied - student not enrolled in course");
        }
    }

    private void throwUnauthorized(String messageKey) {
        throw new UnauthorizedException(messageKey);
    }

    private void throwForbidden(String messageKey) {
        throw new ForbiddenException(messageKey);
    }
}
