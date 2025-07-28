package net.therap.app.mapper;

import net.therap.app.dto.InstructorDTO;
import net.therap.app.model.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author gazizafor
 * @since 28/7/25
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface InstructorMapper {
    
    Instructor toInstructor(InstructorDTO instructorDTO);
    
    InstructorDTO toInstructorDTO(Instructor instructor);
    
    void updateInstructorFromDto(InstructorDTO instructorDTO, @MappingTarget Instructor instructor);
}