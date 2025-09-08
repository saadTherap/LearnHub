package net.therap.learningProcessor.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.service.AuthorizationService;
import net.therap.learningProcessor.service.CourseStudentService;
import net.therap.learningProcessor.service.NotificationService;
import net.therap.learningProcessor.service.StudentSubmissionService;
import net.therap.learningProcessor.util.NotificationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author avidewan
 * @since 7/27/25
 */
@RestController
@RequestMapping("/student-course")
@RequiredArgsConstructor
@Slf4j
public class CourseStudentController {

    private final CourseStudentService courseStudentService;
    private final NotificationService notificationService;
    private final HazelcastCacheService hazelcastCacheService;
    private final AuthorizationService authorizationService;

    @PostMapping("/enrollments")
    public ResponseEntity<Void> enrollInCourse(@RequestParam Long studentId,
                                               @RequestParam Long courseId,
                                               HttpServletRequest request) {

        log.info("[CourseStudentController] Enroll request: studentId={}, courseId={}", studentId, courseId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);
        log.debug("[CourseStudentController] Authorization successful for studentId={}", studentId);

        courseStudentService.enrollInCourse(studentId, courseId);

        log.info("[CourseStudentController] Student {} enrolled in course {}", studentId, courseId);

        notificationService.sendNotification(NotificationUtil.createEnrollmentNotification(studentId, courseId));
        log.debug("[CourseStudentController] Enrollment notification sent for studentId={}, courseId={}", studentId, courseId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/enrollments/course/{courseId}")
    public ResponseEntity<List<StudentDto>> getStudentsEnrolledInCourse(@PathVariable Long courseId,
                                                                        HttpServletRequest request) {

        log.info("[CourseStudentController] Fetch enrolled students for courseId={}", courseId);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE, Map.of("courseId", courseId), request);
        log.debug("[CourseStudentController] Authorization successful for courseId={}", courseId);

        List<StudentDto> cached = hazelcastCacheService.get(CacheConstants.STUDENTS_BY_COURSE, courseId);

        if (Objects.nonNull(cached)) {
            log.info("[CourseStudentController] Returning {} students from cache for courseId={}", cached.size(), courseId);

            return ResponseEntity.ok(cached);
        }

        List<StudentDto> students = courseStudentService.getStudentsEnrolledInCourse(courseId);

        if (Objects.nonNull(students)) {
            hazelcastCacheService.put(CacheConstants.STUDENTS_BY_COURSE, courseId, students);
            log.info("[CourseStudentController] Returning {} students from DB for courseId={}", students.size(), courseId);

            return ResponseEntity.ok(students);
        }

        log.warn("[CourseStudentController] No students found for courseId={}", courseId);

        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/enrollments/course/{courseId}/studentIds")
    public ResponseEntity<List<Long>> getStudentIdsEnrolledInCourse(@PathVariable Long courseId,
                                                                    HttpServletRequest request) {

        log.info("[CourseStudentController] Fetch student IDs for courseId={}", courseId);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE, Map.of("courseId", courseId), request);

        List<Long> ids = courseStudentService.getStudentIdsEnrolledInCourse(courseId);
        log.info("[CourseStudentController] Found {} student IDs for courseId={}", ids.size(), courseId);

        return ResponseEntity.ok(ids);
    }

    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<List<Long>> getEnrolledCourseIdsByStudent(@PathVariable Long studentId,
                                                                    HttpServletRequest request) {

        log.info("[CourseStudentController] Fetch enrolled courses for studentId={}", studentId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);

        List<Long> courseIds = courseStudentService.getEnrolledCourseIdsByStudent(studentId);
        log.info("[CourseStudentController] Student {} enrolled in {} courses", studentId, courseIds.size());

        return ResponseEntity.ok(courseIds);
    }

    @PatchMapping("/student-contents/student/{studentId}/course/{courseId}/content/{contentId}/complete")
    public ResponseEntity<Void> markContentCompleted(@PathVariable Long studentId,
                                                     @PathVariable Long courseId,
                                                     @PathVariable Long contentId,
                                                     HttpServletRequest request) {

        log.info("[CourseStudentController] Mark content complete: studentId={}, courseId={}, contentId={}", studentId, courseId, contentId);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE_OR_STUDENT_ENROLLED_IN_COURSE,
                Map.of("studentId", studentId, "courseId", courseId),
                request);

        boolean success = courseStudentService.completeContent(studentId, contentId);

        if (!success) {
            log.warn("[CourseStudentController] Content completion failed: studentId={}, contentId={}", studentId, contentId);

            return ResponseEntity.notFound().build();
        }

        log.info("[CourseStudentController] Content marked completed: studentId={}, contentId={}", studentId, contentId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/content-status/student/{studentId}")
    public ResponseEntity<List<StudentContentCompletionDto>> getContentStatus(@PathVariable Long studentId,
                                                                              HttpServletRequest request) {

        log.info("[CourseStudentController] Fetch content status for studentId={}", studentId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);

        List<StudentContentCompletionDto> status = courseStudentService.getContentStatusByStudentId(studentId);
        log.info("[CourseStudentController] Found {} content items for studentId={}", status.size(), studentId);

        return ResponseEntity.ok(status);
    }

    @GetMapping("/content-status/student/{studentId}/course/{courseId}/content/{contentId}")
    public ResponseEntity<Boolean> isContentCompleted(@PathVariable Long studentId,
                                      @PathVariable Long courseId,
                                      @PathVariable Long contentId,
                                      HttpServletRequest request) {

        log.info("[CourseStudentController] Check content completion: studentId={}, courseId={}, contentId={}", studentId, courseId, contentId);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE_OR_STUDENT_ENROLLED_IN_COURSE,
                Map.of("courseId", courseId), request);

        boolean completed = courseStudentService.getByStudentIdAndContentId(studentId, contentId) != null;
        log.info("[CourseStudentController] Content completion status: {}", completed);

        return ResponseEntity.ok(completed);
    }

    @PostMapping("/progress/detailed/{studentId}")
    public ResponseEntity<CourseDetailWithProgressDto> getStudentCourseProgressDetail(@PathVariable Long studentId,
                                                                                      @RequestBody CourseDetailWithProgressDto courseDetailWithProgressDto,
                                                                                      HttpServletRequest request) {
        Long courseId = courseDetailWithProgressDto.getId();
        log.info("[CourseStudentController] Fetch detailed course progress for studentId={}, courseId={}", studentId, courseId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);
        authorizationService.authorize(AccessLevel.STUDENT_ENROLLED_IN_COURSE, Map.of("courseId", courseId), request);

        CourseDetailWithProgressDto dto = courseStudentService.getCourseDetailWithProgress(studentId, courseDetailWithProgressDto);
        log.info("[CourseStudentController] Detailed progress fetched for studentId={}, courseId={}", studentId, courseId);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/progress/{studentId}")
    public ResponseEntity<StudentCourseProgressDto> getStudentCourseProgress(
            @PathVariable Long studentId,
            @RequestBody CourseDetailWithProgressDto courseDetailDto,
            HttpServletRequest request) {

        Long courseId = courseDetailDto.getId();
        log.info("[CourseStudentController] Fetch course progress for studentId={}, courseId={}", studentId, courseId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);
        authorizationService.authorize(AccessLevel.STUDENT_ENROLLED_IN_COURSE, Map.of("courseId", courseId), request);

        String cacheKey = studentId + ":" + courseId;
        StudentCourseProgressDto cached = hazelcastCacheService.get(CacheConstants.STUDENT_COURSE_PROGRESS, cacheKey);

        if (Objects.nonNull(cached)) {
            log.info("[CourseStudentController] Returning cached course progress for studentId={}, courseId={}", studentId, courseId);

            return ResponseEntity.ok(cached);
        }

        StudentCourseProgressDto dto = courseStudentService.getStudentCourseProgress(studentId, courseDetailDto);

        if (Objects.nonNull(dto)) {
            hazelcastCacheService.put(CacheConstants.STUDENT_COURSE_PROGRESS, cacheKey, dto);
            log.info("[CourseStudentController] Course progress cached for studentId={}, courseId={}", studentId, courseId);
        }

        return ResponseEntity.ok(dto);
    }


    @PostMapping("/progress/course")
    public ResponseEntity<List<StudentCourseProgressDto>> getAllStudentProgressForCourse(
            @RequestBody CourseDetailWithProgressDto courseDetailWithProgressDto,
            HttpServletRequest request) {

        Long courseId = courseDetailWithProgressDto.getId();
        log.info("[CourseStudentController] Fetch all student progress for courseId={}", courseId);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE, Map.of("courseId", courseId), request);

        List<StudentCourseProgressDto> cached = hazelcastCacheService.get(CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE, courseId);

        if (Objects.nonNull(cached)) {
            log.info("[CourseStudentController] Returning cached progress for {} students, courseId={}", cached.size(), courseId);

            return ResponseEntity.ok(cached);
        }

        List<StudentCourseProgressDto> list = courseStudentService.getAllStudentProgressForCourse(courseDetailWithProgressDto);

        if (Objects.nonNull(list)) {
            hazelcastCacheService.put(CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE, courseId, list);
            log.info("[CourseStudentController] Progress cached for {} students, courseId={}", list.size(), courseId);
        }

        return ResponseEntity.ok(list);
    }



    @PostMapping("/unenroll/student/{studentId}")
    public ResponseEntity<Void> unenrollFromAllCourse(@PathVariable Long studentId,
                                                      HttpServletRequest request) {

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);

        courseStudentService.deleteAllEnrollments(studentId);

        return ResponseEntity.noContent().build();
    }
}