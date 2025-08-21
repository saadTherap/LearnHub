package net.therap.learningProcessor.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.therap.auth.lib.context.UserRequestCache;
import net.therap.auth.lib.util.AuthDataUtil;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.exception.ResourceNotFoundException;
import net.therap.learningProcessor.exception.UnauthorizedException;
import net.therap.learningProcessor.mapper.StudentMapper;
import net.therap.learningProcessor.repository.StudentRepository;
import org.springframework.stereotype.Component;

/**
 * @author avidewan
 * @since 8/20/25
 */
@Component
@RequiredArgsConstructor
public class StudentUtil {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentDto getStudentFromRequest(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            throw new UnauthorizedException("error.auth.required");
        }

        UserRequestCache.UserInfo userInfo = AuthDataUtil.getUserInfo(userId);

        if (userInfo == null || !"STUDENT".equals(userInfo.role())) {
            throw new UnauthorizedException("error.auth.required");
        }

        Student student = studentRepository.findByEmail(userInfo.email())
                .orElseThrow(() -> new ResourceNotFoundException("error.student.notFound", userInfo.email()));
        ;

        return studentMapper.toDto(student);
    }
}

