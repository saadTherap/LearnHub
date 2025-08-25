package net.therap.learningProcessor.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.learningProcessor.dto.StoredFileDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionRequestDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionResultDto;
import net.therap.learningProcessor.dto.content.submission.StudentSubmissionDto;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.service.*;
import net.therap.learningProcessor.util.NotificationUtil;
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

    @PostMapping("/course/{courseId}/generateSignature")
    public ResponseEntity<String> generateSignature(@PathVariable Long courseId,
                                                    @RequestBody StudentSubmissionDto studentSubmissionDto,
                                                    HttpServletRequest request) {

        log.info("[SubmissionController] Generate signature request: courseId={}, submission={}", courseId, studentSubmissionDto);

        authorizationService.authorize(
                AccessLevel.INSTRUCTOR_OF_COURSE_OR_STUDENT_ENROLLED_IN_COURSE,
                Map.of("courseId", courseId),
                request
        );
        log.debug("[SubmissionController] Authorization successful for courseId={}", courseId);

        String signature = submissionService.generateSignature(studentSubmissionDto);
        log.info("[SubmissionController] Generated signature for courseId={}: {}", courseId, signature);

        return ResponseEntity.ok(signature);
    }

    @PostMapping("/assignments")
    public ResponseEntity<StudentSubmissionDto> submitAssignment(
            @RequestParam Long studentId,
            @RequestParam Long contentId,
            @RequestBody StoredFileDto fileDto,
            HttpServletRequest request) {

        log.info("[SubmissionController] Submit assignment request: studentId={}, contentId={}, file={}", studentId, contentId, fileDto);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);
        log.debug("[SubmissionController] Authorization successful for studentId={}", studentId);

        StudentDto studentDto = studentService.getStudentById(studentId);
        StudentSubmissionDto submissionDto = submissionService.submit(studentDto, contentId, fileDto);

        notificationService.sendNotification(NotificationUtil.createSubmissionNotification(submissionDto));
        log.debug("[SubmissionController] Submission notification sent for studentId={}, contentId={}", studentId, contentId);

        log.info("[SubmissionController] Assignment submitted successfully: studentId={}, contentId={}, submissionId={}",
                studentId, contentId, submissionDto.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(submissionDto);
    }

    @PostMapping("/quizzes")
    public ResponseEntity<QuizSubmissionResultDto> submitQuiz(@RequestBody QuizSubmissionRequestDto submissionRequestDto,
                                                              HttpServletRequest request) {

        log.info("[SubmissionController] Quiz submission request: {}", submissionRequestDto);

        authorizationService.authorize(
                AccessLevel.STUDENT_WITH_ID,
                Map.of("studentId", submissionRequestDto.getStudentId()),
                request
        );
        log.debug("[SubmissionController] Authorization successful for studentId={}", submissionRequestDto.getStudentId());

        QuizSubmissionResultDto result = quizService.submitAndEvaluate(submissionRequestDto);
        log.info("[SubmissionController] Quiz submission result for studentId={}: Score: {}", submissionRequestDto.getStudentId(), result.getScorePercentage());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentSubmissionDto>> getSubmissionsByStudent(@PathVariable Long studentId,
                                                                              HttpServletRequest request) {
        log.info("[SubmissionController] Fetch submissions for studentId={}", studentId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);
        log.debug("[SubmissionController] Authorization successful for studentId={}", studentId);

        List<StudentSubmissionDto> submissions = submissionService.getAllByStudentId(studentId);
        log.info("[SubmissionController] Found {} submissions for studentId={}", submissions.size(), studentId);

        return ResponseEntity.ok(submissions);
    }


    @GetMapping("/course/{courseId}/content/{contentId}")
    public ResponseEntity<List<StudentSubmissionDto>> getSubmissionsByContent(@PathVariable Long courseId,
                                                                              @PathVariable Long contentId,
                                                                              HttpServletRequest request) {

        log.info("[SubmissionController] Fetch submissions for courseId={}, contentId={}", courseId, contentId);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE, Map.of("courseId", courseId), request);
        log.debug("[SubmissionController] Authorization successful for courseId={}", courseId);

        List<StudentSubmissionDto> submissions = submissionService.getAllByContentId(contentId);
        log.info("[SubmissionController] Found {} submissions for contentId={}", submissions.size(), contentId);

        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/student/{studentId}/content/{contentId}")
    public ResponseEntity<List<StudentSubmissionDto>> getSubmissionsByStudentAndContent(@PathVariable Long studentId,
                                                                                        @PathVariable Long contentId,
                                                                                        HttpServletRequest request) {

        log.info("[SubmissionController] Fetch submissions for studentId={}, contentId={}", studentId, contentId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", studentId), request);
        log.debug("[SubmissionController] Authorization successful for studentId={}", studentId);

        List<StudentSubmissionDto> submissions = submissionService.getAllByStudentIdAndContentId(studentId, contentId);
        log.info("[SubmissionController] Found {} submissions for studentId={}, contentId={}", submissions.size(), studentId, contentId);

        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/latest/student/{studentId}/course/{courseId}/content/{contentId}")
    public ResponseEntity<StudentSubmissionDto> getLatestSubmissionByStudentAndContent(@PathVariable Long studentId,
                                                                                       @PathVariable Long courseId,
                                                                                       @PathVariable Long contentId,
                                                                                       HttpServletRequest request) {

        log.info("[SubmissionController] Fetch latest submission: studentId={}, courseId={}, contentId={}", studentId, courseId, contentId);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID,
                Map.of("studentId", studentId, "courseId", courseId),
                request);
        log.debug("[SubmissionController] Authorization successful for studentId={}, courseId={}", studentId, courseId);

        Optional<StudentSubmissionDto> latestSubmission = submissionService.getLatestByStudentIdAndContentId(studentId, contentId);

        if (latestSubmission.isEmpty()) {
            log.warn("[SubmissionController] No latest submission found for studentId={}, contentId={}", studentId, contentId);

        } else {
            log.info("[SubmissionController] Latest submission found: submissionId={}", latestSubmission.get().getId());
        }

        return latestSubmission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/latest/course/{courseId}/content/{contentId}")
    public ResponseEntity<List<StudentSubmissionDto>> getLatestSubmissionPerStudentByContent(@PathVariable Long courseId,
                                                                                             @PathVariable Long contentId,
                                                                                             HttpServletRequest request) {

        log.info("[SubmissionController] Fetch latest submissions per student for courseId={}, contentId={}", courseId, contentId);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OF_COURSE, Map.of("courseId", courseId), request);
        log.debug("[SubmissionController] Authorization successful for courseId={}", courseId);

        List<StudentSubmissionDto> latestSubmissions = submissionService.getLatestSubmissionPerStudentByContentId(contentId);
        log.info("[SubmissionController] Found {} latest submissions for contentId={}", latestSubmissions.size(), contentId);

        return ResponseEntity.ok(latestSubmissions);
    }
}