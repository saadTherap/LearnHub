package net.therap.learningProcessor.mapper;

import javax.annotation.processing.Generated;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.Student;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:50+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class StudentMapperImpl implements StudentMapper {

    @Override
    public Student toEntity(StudentDto dto) {
        if ( dto == null ) {
            return null;
        }

        Student student = new Student();

        student.setAddress( dto.getAddress() );
        student.setDateOfBirth( dto.getDateOfBirth() );
        student.setEmail( dto.getEmail() );
        student.setFirstName( dto.getFirstName() );
        student.setGender( mapStringToGender( dto.getGender() ) );
        if ( dto.getId() != null ) {
            student.setId( dto.getId() );
        }
        student.setImageUrl( dto.getImageUrl() );
        student.setLastName( dto.getLastName() );
        student.setPhone( dto.getPhone() );

        return student;
    }

    @Override
    public StudentDto toDto(Student entity) {
        if ( entity == null ) {
            return null;
        }

        StudentDto studentDto = new StudentDto();

        studentDto.setAddress( entity.getAddress() );
        studentDto.setDateOfBirth( entity.getDateOfBirth() );
        studentDto.setEmail( entity.getEmail() );
        studentDto.setFirstName( entity.getFirstName() );
        studentDto.setGender( mapGenderToString( entity.getGender() ) );
        studentDto.setId( entity.getId() );
        studentDto.setImageUrl( entity.getImageUrl() );
        studentDto.setLastName( entity.getLastName() );
        studentDto.setPhone( entity.getPhone() );

        return studentDto;
    }

    @Override
    public void updateStudentFromDto(StudentDto dto, Student entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getAddress() != null ) {
            entity.setAddress( dto.getAddress() );
        }
        if ( dto.getDateOfBirth() != null ) {
            entity.setDateOfBirth( dto.getDateOfBirth() );
        }
        if ( dto.getEmail() != null ) {
            entity.setEmail( dto.getEmail() );
        }
        if ( dto.getFirstName() != null ) {
            entity.setFirstName( dto.getFirstName() );
        }
        if ( dto.getGender() != null ) {
            entity.setGender( mapStringToGender( dto.getGender() ) );
        }
        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getImageUrl() != null ) {
            entity.setImageUrl( dto.getImageUrl() );
        }
        if ( dto.getLastName() != null ) {
            entity.setLastName( dto.getLastName() );
        }
        if ( dto.getPhone() != null ) {
            entity.setPhone( dto.getPhone() );
        }
    }
}
