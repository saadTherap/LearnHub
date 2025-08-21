package net.therap.learningProcessor.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.dto.StoredFileDto;
import lombok.extern.slf4j.Slf4j;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionRequestDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionResultDto;
import net.therap.learningProcessor.dto.content.submission.StudentSubmissionDto;
import net.therap.learningProcessor.entity.SubmissionNotification;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.eum.NotificationType;
import net.therap.learningProcessor.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author avidewan
 * @since 8/7/25
 */
@Slf4j
@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final StudentSubmissionService submissionService;
    private final QuizService quizService;
    private final StudentService studentService;
    private final NotificationService notificationService;
    private final AuthorizationService authorizationService;

    @PostMapping("/course/{id}/generateSignature")
    public ResponseEntity<String> generateSignature(@RequestBody StudentSubmissionDto studentSubmissionDto, @PathVariable String courseId) {
        return ResponseEntity.ok(submissionService.generateSignature(studentSubmissionDto));
    }

    @PostMapping("/assignments")
    public ResponseEntity<StudentSubmissionDto> submitAssignment(
            @RequestParam Long studentId,
            @RequestParam Long contentId,
            @RequestBody StoredFileDto fileDto,
            HttpServletRequest request) {

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);

        StudentDto studentDto = studentService.getStudentById(studentId);

        StudentSubmissionDto submissionDto = submissionService.submit(studentDto, contentId, fileDto);

        SubmissionNotification notification = new SubmissionNotification();
        notification.setType(NotificationType.SUBMISSION);
        notification.setSubmissionId(submissionDto.getId());
        notification.setMessage("An student has made a submission into content Id: " + submissionDto.getContentId());
        notificationService.sendNotification(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(submissionDto);
    }

    @PostMapping("/quizzes")
    public ResponseEntity<QuizSubmissionResultDto> submitQuiz(@RequestBody QuizSubmissionRequestDto submissionRequestDto,
                                                              HttpServletRequest request) {

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", submissionRequestDto.getStudentId()), request);

        log.info("Quiz Submission RequestDto: {}", submissionRequestDto);

        QuizSubmissionResultDto result = quizService.submitAndEvaluate(submissionRequestDto);

        log.info("Result: {}", result);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentSubmissionDto>> getSubmissionsByStudent(@PathVariable Long studentId,
                                                                              HttpServletRequest request) {
        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);

        List<StudentSubmissionDto> submissions = submissionService.getAllByStudentId(studentId);

        return ResponseEntity.ok(submissions);
    }


    @GetMapping("/course/{courseId}/content/{contentId}")
    public ResponseEntity<List<StudentSubmissionDto>> getSubmissionsByContent(@PathVariable Long courseId,
                                                                              @PathVariable Long contentId,
                                                                              HttpServletRequest request) {

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE,
                Map.of("courseId", courseId),
                request);

        List<StudentSubmissionDto> submissions = submissionService.getAllByContentId(contentId);

        return ResponseEntity.ok(submissions);
    }


    @GetMapping("/student/{studentId}/content/{contentId}")
    public ResponseEntity<List<StudentSubmissionDto>> getSubmissionsByStudentAndContent(@PathVariable Long studentId,
                                                                                        @PathVariable Long contentId,
                                                                                        HttpServletRequest request) {

        log.info("Student Submission Requested for: studentId-{} and contentId- {}", studentId, contentId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID,
                Map.of("studentId", studentId),
                request);

        List<StudentSubmissionDto> submissions = submissionService.getAllByStudentIdAndContentId(studentId, contentId);

        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/latest/student/{studentId}/course/{courseId}/content/{contentId}")
    public ResponseEntity<StudentSubmissionDto> getLatestSubmissionByStudentAndContent(@PathVariable Long studentId,
                                                                                       @PathVariable Long courseId,
                                                                                       @PathVariable Long contentId,
                                                                                       HttpServletRequest request) {

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE_OR_STUDENT_WITH_ID,
                Map.of("studentId", studentId, "courseId", courseId),
                request);

        Optional<StudentSubmissionDto> latestSubmission = submissionService.getLatestByStudentIdAndContentId(studentId, contentId);

        return latestSubmission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/latest/course/{courseId}/content/{contentId}")
    public ResponseEntity<List<StudentSubmissionDto>> getLatestSubmissionPerStudentByContent(@PathVariable Long courseId,
                                                                                             @PathVariable Long contentId,
                                                                                             HttpServletRequest request) {

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE_OR_STUDENT_WITH_ID,
                Map.of("courseId", courseId),
                request);

        List<StudentSubmissionDto> latestSubmissions = submissionService.getLatestSubmissionPerStudentByContentId(contentId);

        return ResponseEntity.ok(latestSubmissions);
    }
}