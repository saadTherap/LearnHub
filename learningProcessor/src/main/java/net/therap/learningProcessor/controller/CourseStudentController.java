package net.therap.learningProcessor.controller;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.EnrollmentNotification;
import net.therap.learningProcessor.entity.SubmissionNotification;
import net.therap.learningProcessor.eum.NotificationType;
import net.therap.learningProcessor.service.CourseStudentService;
import net.therap.learningProcessor.service.HazelcastCacheService;
import net.therap.learningProcessor.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@RestController
@RequestMapping("/student-course")
@RequiredArgsConstructor
public class CourseStudentController {

    private final CourseStudentService courseStudentService;
    private final NotificationService notificationService;
    private final HazelcastCacheService hazelcastCacheService;

    @PostMapping("/enrollments")
    public ResponseEntity<Void> enrollInCourse(@RequestParam Long studentId,
                                               @RequestParam Long courseId) {
        courseStudentService.enrollInCourse(studentId, courseId);

        EnrollmentNotification notification = new EnrollmentNotification();
        notification.setType(NotificationType.ENROLLMENT);
        notification.setStudentId(studentId);
        notification.setCourseId(courseId);
        notification.setMessage("An student has enrolled into Course Id: " + courseId);
        notificationService.sendNotification(notification);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/enrollments/course/{courseId}")
    public ResponseEntity<List<StudentDto>> getStudentsEnrolledInCourse(@PathVariable Long courseId) {
        List<StudentDto> cached = hazelcastCacheService.get(CacheConstants.STUDENTS_BY_COURSE, courseId);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        List<StudentDto> students = courseStudentService.getStudentsEnrolledInCourse(courseId);

        if (students != null) {
            hazelcastCacheService.put(CacheConstants.STUDENTS_BY_COURSE, courseId, students);
            return ResponseEntity.ok(students);
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/enrollments/course/{courseId}/studentIds")
    public ResponseEntity<List<Long>> getStudentIdsEnrolledInCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseStudentService.getStudentIdsEnrolledInCourse(courseId));
    }

    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<List<Long>> getEnrolledCourseIdsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(courseStudentService.getEnrolledCourseIdsByStudent(studentId));
    }

    @PatchMapping("/student-contents/{studentId}/{contentId}/complete")
    public ResponseEntity<Void> markContentCompleted(@PathVariable Long studentId, @PathVariable Long contentId) {
        boolean success = courseStudentService.completeContent(studentId, contentId);

        if (!success) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/content-status/{studentId}")
    public ResponseEntity<List<StudentContentCompletionDto>> getContentStatus(@PathVariable Long studentId) {

        return ResponseEntity.ok(courseStudentService.getContentStatusByStudentId(studentId));
    }

    @GetMapping("/progress/detailed/{studentId}/{courseId}")
    public ResponseEntity<CourseDetailWithProgressDto> getStudentCourseProgressDetail(@PathVariable Long studentId, @PathVariable Long courseId) {

        String cacheKey = studentId + ":" + courseId;
        CourseDetailWithProgressDto cached = hazelcastCacheService.get(CacheConstants.COURSE_PROGRESS_DETAIL, cacheKey);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        CourseDetailWithProgressDto dto = courseStudentService.getCourseDetailWithProgress(studentId, courseId);
        if (dto != null) {
            hazelcastCacheService.put(CacheConstants.COURSE_PROGRESS_DETAIL, cacheKey, dto);
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/progress/{studentId}/{courseId}")
    public ResponseEntity<StudentCourseProgressDto> getStudentCourseProgress(@PathVariable Long studentId, @PathVariable Long courseId) {
        String cacheKey = studentId + ":" + courseId;
        StudentCourseProgressDto cached = hazelcastCacheService.get(CacheConstants.STUDENT_COURSE_PROGRESS, cacheKey);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        StudentCourseProgressDto dto = courseStudentService.getStudentCourseProgress(studentId, courseId);
        if (dto != null) {
            hazelcastCacheService.put(CacheConstants.STUDENT_COURSE_PROGRESS, cacheKey, dto);
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/progress/course/{courseId}")
    public ResponseEntity<List<StudentCourseProgressDto>> getAllStudentProgressForCourse(@PathVariable Long courseId) {
        List<StudentCourseProgressDto> cached = hazelcastCacheService.get(CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE, courseId);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        List<StudentCourseProgressDto> list = courseStudentService.getAllStudentProgressForCourse(courseId);
        if (list != null) {
            hazelcastCacheService.put(CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE, courseId, list);
        }

        return ResponseEntity.ok(list);
    }
}