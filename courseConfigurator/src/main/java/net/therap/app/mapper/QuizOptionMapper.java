package net.therap.app.mapper;

import net.therap.app.dto.QuizOptionDTO;
import net.therap.app.model.QuizOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface QuizOptionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quizQuestion", ignore = true)
    QuizOption toQuizOption(QuizOptionDTO dto);
}