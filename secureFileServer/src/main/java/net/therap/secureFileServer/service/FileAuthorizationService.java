package net.therap.secureFileServer.service;

import lombok.RequiredArgsConstructor;
import net.therap.secureFileServer.entity.StoredFile;
import net.therap.secureFileServer.repository.CourseEnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author avidewan
 * @since 8/14/25
 */
@Service
@RequiredArgsConstructor
public class FileAuthorizationService {

    private final CourseEnrollmentRepository courseEnrollmentRepository;

    public boolean canAccessFile(StoredFile file, Long userId, String userRole) {

        boolean access = false;

        switch (userRole) {

            case "ADMIN":
               access = true;

               break;

            case "STUDENT":
                access = checkStudentAccessFile(file, userId);

                break;

            case "INSTRUCTOR":
                access = checkInstructorAccessFile(file, userId);

        }

        return access;
    }

    private boolean checkStudentAccessFile(StoredFile file, Long studentId) {

        if (Objects.equals(file.getUploaderId(), studentId)) {
            return true;
        }

        Long courseId = file.getContextId();
        
        return courseEnrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    private boolean checkInstructorAccessFile(StoredFile file, Long teacherId) {

        return false;
    }
}