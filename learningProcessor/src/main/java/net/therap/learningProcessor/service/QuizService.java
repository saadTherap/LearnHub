package net.therap.learningProcessor.service;

import net.therap.learningProcessor.dto.content.quiz.QuizDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionRequestDto;
import net.therap.learningProcessor.dto.content.quiz.QuizSubmissionResultDto;

/**
 * @author avidewan
 * @since 8/10/25
 */
public interface QuizService {

    public QuizSubmissionResultDto submitAndEvaluate(QuizSubmissionRequestDto submissionRequestDto);
}