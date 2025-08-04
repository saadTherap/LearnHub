package net.therap.learningProcessor.mapper;

import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.eum.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author avidewan
 * @since 8/4/25
 */
public class StudentMapperTest {

    private StudentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(StudentMapper.class);
    }

    @Test
    void toDto_shouldMapCorrectly() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Avi");
        student.setGender(Gender.MALE);

        StudentDto dto = mapper.toDto(student);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getGender()).isEqualTo("MALE");
    }

    @Test
    void toEntity_shouldMapCorrectly() {
        StudentDto dto = new StudentDto();
        dto.setFirstName("Ann");
        dto.setGender("female");

        Student entity = mapper.toEntity(dto);

        assertThat(entity.getFirstName()).isEqualTo("Ann");
        assertThat(entity.getGender()).isEqualTo(Gender.FEMALE);
    }

    @Test
    void updateStudentFromDto_shouldIgnoreNulls() {
        Student student = new Student();
        student.setFirstName("OldName");
        student.setGender(Gender.MALE);
        student.setEmail("old@email.com");

        StudentDto dto = new StudentDto();
        dto.setFirstName("NewName");
        dto.setGender(null);
        dto.setEmail(null);

        mapper.updateStudentFromDto(dto, student);

        assertThat(student.getFirstName()).isEqualTo("NewName");
        assertThat(student.getGender()).isEqualTo(Gender.MALE);
        assertThat(student.getEmail()).isEqualTo("old@email.com");
    }

    @Test
    void mapStringToGender_shouldWorkCorrectly() {
        assertThat(mapper.mapStringToGender("male")).isEqualTo(Gender.MALE);
        assertThat(mapper.mapStringToGender(null)).isNull();
    }

    @Test
    void mapGenderToString_shouldWorkCorrectly() {
        assertThat(mapper.mapGenderToString(Gender.FEMALE)).isEqualTo("FEMALE");
        assertThat(mapper.mapGenderToString(null)).isNull();
    }
}