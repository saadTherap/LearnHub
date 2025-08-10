package net.therap.app.mapper;

import javax.annotation.processing.Generated;
import net.therap.app.dto.QuizOptionDTO;
import net.therap.app.model.QuizOption;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:49+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class QuizOptionMapperImpl implements QuizOptionMapper {

    @Override
    public QuizOption toQuizOption(QuizOptionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        QuizOption quizOption = new QuizOption();

        quizOption.setCorrect( dto.isCorrect() );
        quizOption.setOptionText( dto.getOptionText() );

        return quizOption;
    }
}
