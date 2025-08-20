package net.therap.learningProcessor.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.EnrollmentNotification;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.eum.NotificationType;
import net.therap.learningProcessor.service.AuthorizationService;
import net.therap.learningProcessor.service.CourseStudentService;
import net.therap.learningProcessor.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private final AuthorizationService authorizationService;

    @PostMapping("/enrollments")
    public ResponseEntity<Void> enrollInCourse(@RequestParam Long studentId,
                                               @RequestParam Long courseId,
                                               HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID,  Map.of("studentId", studentId), request);

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
    public ResponseEntity<List<StudentDto>> getStudentsEnrolledInCourse(@PathVariable Long courseId,
                                                                        HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.TEACHER_ONLY,  request);

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
    public ResponseEntity<List<Long>> getStudentIdsEnrolledInCourse(@PathVariable Long courseId,
                                                                    HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.TEACHER_ONLY,  request);

        return ResponseEntity.ok(courseStudentService.getStudentIdsEnrolledInCourse(courseId));
    }

    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<List<Long>> getEnrolledCourseIdsByStudent(@PathVariable Long studentId,
                                                                    HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID,  Map.of("studentId", studentId), request);

        return ResponseEntity.ok(courseStudentService.getEnrolledCourseIdsByStudent(studentId));
    }

    @PatchMapping("/student-contents/{studentId}/{contentId}/complete")
    public ResponseEntity<Void> markContentCompleted(@PathVariable Long studentId,
                                                     @PathVariable Long contentId,
                                                     HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.TEACHER_AND_STUDENT_WITH_ID,  Map.of("studentId", studentId), request);

        boolean success = courseStudentService.completeContent(studentId, contentId);

        if (!success) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/content-status/{studentId}")
    public ResponseEntity<List<StudentContentCompletionDto>> getContentStatus(@PathVariable Long studentId,
                                                                              HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.TEACHER_AND_STUDENT_WITH_ID,  Map.of("studentId", studentId), request);

        return ResponseEntity.ok(courseStudentService.getContentStatusByStudentId(studentId));
    }

    @PostMapping("/progress/detailed/{studentId}")
    public ResponseEntity<CourseDetailWithProgressDto> getStudentCourseProgressDetail(@PathVariable Long studentId,
                                                                                      @RequestBody CourseDetailWithProgressDto courseDetailWithProgressDto,
                                                                                      HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.TEACHER_AND_STUDENT_WITH_ID,  Map.of("studentId", studentId), request);

        CourseDetailWithProgressDto dto = courseStudentService.getCourseDetailWithProgress(studentId, courseDetailWithProgressDto);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/progress/{studentId}")
    public ResponseEntity<StudentCourseProgressDto> getStudentCourseProgress(
            @PathVariable Long studentId,
            @RequestBody CourseDetailWithProgressDto courseDetailDto,
            HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.TEACHER_AND_STUDENT_WITH_ID,  Map.of("studentId", studentId), request);

        Long courseId = courseDetailDto.getId();

        String cacheKey = studentId + ":" + courseId;
        StudentCourseProgressDto cached = hazelcastCacheService.get(CacheConstants.STUDENT_COURSE_PROGRESS, cacheKey);

        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        StudentCourseProgressDto dto = courseStudentService.getStudentCourseProgress(studentId, courseDetailDto);

        if (dto != null) {
            hazelcastCacheService.put(CacheConstants.STUDENT_COURSE_PROGRESS, cacheKey, dto);
        }

        return ResponseEntity.ok(dto);
    }

//    @GetMapping("/progress/course/{courseId}")
//    public ResponseEntity<List<StudentCourseProgressDto>> getAllStudentProgressForCourse(@PathVariable Long courseId) {
//        List<StudentCourseProgressDto> cached = hazelcastCacheService.get(CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE, courseId);
//        if (cached != null) {
//            return ResponseEntity.ok(cached);
//        }
//
//        List<StudentCourseProgressDto> list = courseStudentService.getAllStudentProgressForCourse(courseId);
//        if (list != null) {
//            hazelcastCacheService.put(CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE, courseId, list);
//        }
//
//        return ResponseEntity.ok(list);
//    }

    @PostMapping("/progress/course")
    public ResponseEntity<List<StudentCourseProgressDto>> getAllStudentProgressForCourse(
            @RequestBody CourseDetailWithProgressDto courseDetailWithProgressDto,
            HttpServletRequest request) {

//        authorizationService.authorize(AccessLevel.TEACHER_ONLY,  request);

        Long courseId = courseDetailWithProgressDto.getId();

        List<StudentCourseProgressDto> cached = hazelcastCacheService.get(
                CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE,
                courseId
        );

        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        List<StudentCourseProgressDto> list = courseStudentService.getAllStudentProgressForCourse(courseDetailWithProgressDto);

        if (list != null) {
            hazelcastCacheService.put(CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE, courseId, list);
        }

        return ResponseEntity.ok(list);
    }
}