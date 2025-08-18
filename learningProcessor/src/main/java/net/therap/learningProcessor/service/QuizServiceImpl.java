package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
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

    private final CourseStudentService courseStudentService;

    public QuizSubmissionResultDto submitAndEvaluate(QuizSubmissionRequestDto request) {
        QuizSubmissionResultDto result = QuizEvaluationUtil.evaluate(request.getQuizDto(), request);

        if (result.isPassed()) {
            courseStudentService.completeContent(request.getStudentId(), request.getContentId());
        }

        return result;
    }
}