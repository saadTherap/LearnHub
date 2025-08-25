package net.therap.learningProcessor.util;

import net.therap.learningProcessor.dto.content.quiz.QuizDto;
import net.therap.learningProcessor.dto.content.quiz.QuizOptionDto;
import net.therap.learningProcessor.dto.content.quiz.QuizQuestionDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionRequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author avidewan
 * @since 8/10/25
 */
class QuizEvaluationUtilTest {

    @Test
    void evaluate_allCorrect_shouldPass() {
        QuizDto quiz = new QuizDto();
        quiz.setQuestions(List.of(
                question(1L, List.of(option(11L, true), option(12L, false))),
                question(2L, List.of(option(21L, true), option(22L, false), option(23L, true)))
        ));

        QuizSubmissionRequestDto submission = new QuizSubmissionRequestDto();
        submission.setStudentId(101L);
        submission.setContentId(501L);
        submission.setAnswers(Map.of(
                1L, List.of(11L),
                2L, List.of(21L, 23L)
        ));

        var result = QuizEvaluationUtil.evaluate(quiz, submission);

        assertThat(result.isPassed()).isTrue();
        assertThat(result.getScorePercentage()).isEqualTo(100.0);
        assertThat(result.getStudentId()).isEqualTo(101L);
        assertThat(result.getContentId()).isEqualTo(501L);
    }

    @Test
    void evaluate_partialCorrect_shouldFail() {
        QuizDto quiz = new QuizDto();
        quiz.setQuestions(List.of(
                question(1L, List.of(option(11L, true), option(12L, false))),
                question(2L, List.of(option(21L, true), option(22L, false), option(23L, true)))
        ));

        QuizSubmissionRequestDto submission = new QuizSubmissionRequestDto();
        submission.setStudentId(101L);
        submission.setContentId(501L);
        submission.setAnswers(Map.of(
                1L, List.of(11L),
                2L, List.of(21L)
        ));

        var result = QuizEvaluationUtil.evaluate(quiz, submission);

        assertThat(result.isPassed()).isFalse();
        assertThat(result.getScorePercentage()).isEqualTo(50.0);
    }

    @Test
    void evaluate_noAnswers_shouldFail() {
        QuizDto quiz = new QuizDto();
        quiz.setQuestions(List.of(
                question(1L, List.of(option(11L, true), option(12L, false)))
        ));

        QuizSubmissionRequestDto submission = new QuizSubmissionRequestDto();
        submission.setStudentId(101L);
        submission.setContentId(501L);
        submission.setAnswers(Map.of());

        var result = QuizEvaluationUtil.evaluate(quiz, submission);

        assertThat(result.isPassed()).isFalse();
        assertThat(result.getScorePercentage()).isEqualTo(0.0);
    }

    @Test
    void evaluate_emptyQuiz_shouldReturnZeroAndFail() {
        QuizDto quiz = new QuizDto();
        quiz.setQuestions(List.of());

        QuizSubmissionRequestDto submission = new QuizSubmissionRequestDto();
        submission.setStudentId(101L);
        submission.setContentId(501L);
        submission.setAnswers(Map.of());

        var result = QuizEvaluationUtil.evaluate(quiz, submission);

        assertThat(result.isPassed()).isFalse();
        assertThat(result.getScorePercentage()).isEqualTo(0.0);
    }


    private QuizQuestionDto question(long id, List<QuizOptionDto> options) {
        QuizQuestionDto question = new QuizQuestionDto();
        question.setId(id);
        question.setOptions(options);

        return question;
    }

    private QuizOptionDto option(long id, boolean correct) {
        QuizOptionDto option = new QuizOptionDto();
        option.setId(id);
        option.setCorrect(correct);

        return option;
    }
}
