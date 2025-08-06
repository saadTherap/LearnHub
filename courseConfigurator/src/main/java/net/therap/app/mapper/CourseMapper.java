package net.therap.app.mapper;

import net.therap.app.dto.CourseDTO;
import net.therap.app.helper.InstructorMappingHelper;
import net.therap.app.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
* @author gazizafor
* @since 30/7/25
*/
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {InstructorMappingHelper.class}
)
public interface CourseMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentRelease", ignore = true)
    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "instructor", source = "instructorId")
    Course toCourse(CourseDTO courseDTO);
    
    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "instructorName", source = "instructor.name")
    CourseDTO toCourseDTO(Course course);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentRelease", ignore = true)
    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    void updateCourseFromCourseDTO(CourseDTO courseDTO, @MappingTarget Course course);
}