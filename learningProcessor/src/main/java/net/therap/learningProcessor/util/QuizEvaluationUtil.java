package net.therap.learningProcessor.util;


import net.therap.learningProcessor.dto.content.quiz.QuizDto;
import net.therap.learningProcessor.dto.content.quiz.QuizOptionDto;
import net.therap.learningProcessor.dto.content.quiz.QuizQuestionDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionRequestDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionResultDto;

import java.util.HashSet;
import java.util.List;

/**
 * @author avidewan
 * @since 8/10/25
 */
public class QuizEvaluationUtil {

    private static final double PASS_MARKS_PERCENT = 60.0;

    public static QuizSubmissionResultDto evaluate(QuizDto quiz, QuizSubmissionRequestDto submissionRequestDto) {
        int totalQuestions = quiz.getQuestions().size();
        int correctCount = 0;

        for (QuizQuestionDto question : quiz.getQuestions()) {
            List<Long> correctOptionIds = question.getOptions().stream()
                    .filter(QuizOptionDto::isCorrect)
                    .map(QuizOptionDto::getId)
                    .toList();

            List<Long> submittedOptionIds = submissionRequestDto.getAnswers().getOrDefault(question.getId(), List.of());

            if (new HashSet<>(correctOptionIds).equals(new HashSet<>(submittedOptionIds))) {
                correctCount++;
            }
        }

        double percentage = totalQuestions == 0 ? 0.0 : (correctCount * 100.0) / totalQuestions;
        boolean passed = percentage >= PASS_MARKS_PERCENT;

        return QuizSubmissionResultDto.builder()
                .studentId(submissionRequestDto.getStudentId())
                .contentId(submissionRequestDto.getContentId())
                .scorePercentage(percentage)
                .passed(passed)
                .build();
    }
}