package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.dto.content.quiz.QuizDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionRequestDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionResultDto;
import net.therap.learningProcessor.util.QuizEvaluationUtil;
import org.springframework.stereotype.Service;

/**
 * @author avidewan
 * @since 8/10/25
 */
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService{

    private final CourseClient courseClient;
    private final CourseStudentService courseStudentService;

    public QuizSubmissionResultDto submitAndEvaluate(QuizSubmissionRequestDto request) {
        QuizDto quizDto = (QuizDto) courseClient.getContentDetail(request.getContentId());

        QuizSubmissionResultDto result = QuizEvaluationUtil.evaluate(quizDto, request);

        if (result.isPassed()) {
            courseStudentService.completeContent(request.getStudentId(), request.getContentId());
        }

        return result;
    }
}