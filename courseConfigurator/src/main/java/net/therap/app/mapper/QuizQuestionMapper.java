package net.therap.app.mapper;

import net.therap.app.dto.QuizOptionDTO;
import net.therap.app.dto.QuizQuestionDTO;
import net.therap.app.helper.QuizQuestionMappingHelper;
import net.therap.app.model.QuizOption;
import net.therap.app.model.QuizQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {QuizQuestionMappingHelper.class}
)
public interface QuizQuestionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quiz", source = "quizId")
    @Mapping(target = "options", ignore = true)
    QuizQuestion toQuizQuestion(QuizQuestionDTO dto);
}