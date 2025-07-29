package net.therap.learningProcessor.mapper;

import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentMapper {

    Student toEntity(StudentDto dto);

    StudentDto toDto(Student entity);

    void updateStudentFromDto(StudentDto dto, @MappingTarget Student entity);
}