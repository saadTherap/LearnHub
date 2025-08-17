//package net.therap.secureFileServer.service;
//
//import lombok.RequiredArgsConstructor;
//import net.therap.secureFileServer.entity.course.Course;
//import net.therap.secureFileServer.entity.primary.StoredFile;
//import net.therap.secureFileServer.repository.course.CourseRepository;
//import net.therap.secureFileServer.repository.primary.CourseEnrollmentRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.Objects;
//
///**
// * @author avidewan
// * @since 8/14/25
// */
//@Service
//@RequiredArgsConstructor
//public class FileAuthorizationService {
//
//    private static final String ADMIN = "ADMIN";
//    private static final String STUDENT = "STUDENT";
//    private static final String INSTRUCTOR = "INSTRUCTOR";
//
//    private final CourseEnrollmentRepository courseEnrollmentRepository;
//
//    private final CourseRepository courseRepository;
//
//    public boolean canAccessFile(StoredFile file, Long userId, String userRole) {
//
//        if (file == null || userId == null || userRole == null) {
//            return false;
//        }
//
//        return switch (userRole.toUpperCase()) {
//            case ADMIN -> true;
//            case STUDENT -> canStudentAccessFile(file, userId);
//            case INSTRUCTOR -> canInstructorAccessFile(file, userId);
//            default -> false;
//        };
//    }
//
//    private boolean canStudentAccessFile(StoredFile file, Long studentId) {
//        if (isFileOwner(file, studentId)) {
//            return true;
//        }
//
//        if (isUploadedByStudent(file)) {
//            return false;
//        }
//
//        return isStudentEnrolledInCourse(studentId, file.getContextId());
//    }
//
//    private boolean canInstructorAccessFile(StoredFile file, Long instructorId) {
//        if (isFileOwner(file, instructorId)) {
//            return true;
//        }
//
//        return isInstructorOfCourse(instructorId, file.getContextId());
//    }
//
//    private boolean isFileOwner(StoredFile file, Long userId) {
//        return Objects.equals(file.getUploaderId(), userId);
//    }
//
//    private boolean isUploadedByStudent(StoredFile file) {
//        return STUDENT.equalsIgnoreCase(file.getUploaderRole());
//    }
//
//    private boolean isStudentEnrolledInCourse(Long studentId, Long courseId) {
//        if (courseId == null) {
//            return false;
//        }
//        return courseEnrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
//    }
//
//    private boolean isInstructorOfCourse(Long instructorId, Long courseId) {
//        if (courseId == null) {
//            return false;
//        }
//        return courseRepository.findById(courseId)
//                .map(course -> Objects.equals(course.getInstructorId(), instructorId))
//                .orElse(false);
//    }
//}