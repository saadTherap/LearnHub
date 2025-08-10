package net.therap.app.mapper;

import javax.annotation.processing.Generated;
import net.therap.app.dto.QuizQuestionDTO;
import net.therap.app.helper.QuizQuestionMappingHelper;
import net.therap.app.model.QuizQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:49+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class QuizQuestionMapperImpl implements QuizQuestionMapper {

    @Autowired
    private QuizQuestionMappingHelper quizQuestionMappingHelper;

    @Override
    public QuizQuestion toQuizQuestion(QuizQuestionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        QuizQuestion quizQuestion = new QuizQuestion();

        if ( dto.getQuizId() != null ) {
            quizQuestion.setQuiz( quizQuestionMappingHelper.map( dto.getQuizId().longValue() ) );
        }
        quizQuestion.setQuestionText( dto.getQuestionText() );

        return quizQuestion;
    }
}
