package net.therap.app.mapper;

import javax.annotation.processing.Generated;
import net.therap.app.dto.InstructorDTO;
import net.therap.app.dto.InstructorDtoCatalog;
import net.therap.app.model.Instructor;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:49+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class InstructorMapperImpl implements InstructorMapper {

    @Override
    public Instructor toInstructor(InstructorDTO instructorDTO) {
        if ( instructorDTO == null ) {
            return null;
        }

        Instructor instructor = new Instructor();

        instructor.setDateOfBirth( instructorDTO.getDateOfBirth() );
        instructor.setEmail( instructorDTO.getEmail() );
        instructor.setId( instructorDTO.getId() );
        instructor.setImageUrl( instructorDTO.getImageUrl() );
        instructor.setName( instructorDTO.getName() );

        return instructor;
    }

    @Override
    public InstructorDTO toInstructorDTO(Instructor instructor) {
        if ( instructor == null ) {
            return null;
        }

        InstructorDTO instructorDTO = new InstructorDTO();

        instructorDTO.setDateOfBirth( instructor.getDateOfBirth() );
        instructorDTO.setEmail( instructor.getEmail() );
        instructorDTO.setId( instructor.getId() );
        instructorDTO.setImageUrl( instructor.getImageUrl() );
        instructorDTO.setName( instructor.getName() );

        return instructorDTO;
    }

    @Override
    public void updateInstructorFromDto(InstructorDTO instructorDTO, Instructor instructor) {
        if ( instructorDTO == null ) {
            return;
        }

        if ( instructorDTO.getDateOfBirth() != null ) {
            instructor.setDateOfBirth( instructorDTO.getDateOfBirth() );
        }
        if ( instructorDTO.getImageUrl() != null ) {
            instructor.setImageUrl( instructorDTO.getImageUrl() );
        }
        if ( instructorDTO.getName() != null ) {
            instructor.setName( instructorDTO.getName() );
        }
    }

    @Override
    public InstructorDtoCatalog toInstructorDtoCatalog(Instructor instructor) {
        if ( instructor == null ) {
            return null;
        }

        InstructorDtoCatalog instructorDtoCatalog = new InstructorDtoCatalog();

        instructorDtoCatalog.setEmail( instructor.getEmail() );
        instructorDtoCatalog.setId( instructor.getId() );
        instructorDtoCatalog.setImageUrl( instructor.getImageUrl() );
        instructorDtoCatalog.setName( instructor.getName() );

        return instructorDtoCatalog;
    }
}
