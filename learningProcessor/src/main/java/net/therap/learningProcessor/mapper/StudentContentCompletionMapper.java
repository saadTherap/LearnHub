package net.therap.learningProcessor.mapper;

import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.entity.StudentContentCompletion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author avidewan
 * @since 8/25/25
 */
@Mapper(componentModel = "spring")
public interface StudentContentCompletionMapper {

    StudentContentCompletionMapper INSTANCE = Mappers.getMapper(StudentContentCompletionMapper.class);

    @Mapping(source = "student.id", target = "studentId")
    StudentContentCompletionDto toDto(StudentContentCompletion entity);

    @Mapping(source = "studentId", target = "student.id")
    StudentContentCompletion toEntity(StudentContentCompletionDto dto);
}