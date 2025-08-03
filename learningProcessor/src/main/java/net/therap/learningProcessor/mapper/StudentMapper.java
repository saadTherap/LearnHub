package net.therap.learningProcessor.mapper;

import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.eum.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Objects;

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

    default Gender mapStringToGender(String gender) {
        return (Objects.isNull(gender)) ? null : Gender.valueOf(gender.toUpperCase());
    }

    default String mapGenderToString(Gender gender) {
        return (Objects.isNull(gender)) ? null : gender.name();
    }
}