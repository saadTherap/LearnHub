package net.therap.app.mapper;

import javax.annotation.processing.Generated;
import net.therap.app.dto.CourseDTO;
import net.therap.app.helper.InstructorMappingHelper;
import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:49+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class CourseMapperImpl implements CourseMapper {

    @Autowired
    private InstructorMappingHelper instructorMappingHelper;

    @Override
    public Course toCourse(CourseDTO courseDTO) {
        if ( courseDTO == null ) {
            return null;
        }

        Course course = new Course();

        course.setInstructor( instructorMappingHelper.map( courseDTO.getInstructorId() ) );
        course.setDescription( courseDTO.getDescription() );
        course.setName( courseDTO.getName() );

        return course;
    }

    @Override
    public CourseDTO toCourseDTO(Course course) {
        if ( course == null ) {
            return null;
        }

        CourseDTO courseDTO = new CourseDTO();

        courseDTO.setInstructorId( courseInstructorId( course ) );
        courseDTO.setInstructorName( courseInstructorName( course ) );
        courseDTO.setCurrentRelease( course.getCurrentRelease() );
        courseDTO.setDescription( course.getDescription() );
        courseDTO.setId( course.getId() );
        courseDTO.setName( course.getName() );

        return courseDTO;
    }

    @Override
    public void updateCourseFromCourseDTO(CourseDTO courseDTO, Course course) {
        if ( courseDTO == null ) {
            return;
        }

        if ( courseDTO.getDescription() != null ) {
            course.setDescription( courseDTO.getDescription() );
        }
        if ( courseDTO.getName() != null ) {
            course.setName( courseDTO.getName() );
        }
    }

    private long courseInstructorId(Course course) {
        if ( course == null ) {
            return 0L;
        }
        Instructor instructor = course.getInstructor();
        if ( instructor == null ) {
            return 0L;
        }
        long id = instructor.getId();
        return id;
    }

    private String courseInstructorName(Course course) {
        if ( course == null ) {
            return null;
        }
        Instructor instructor = course.getInstructor();
        if ( instructor == null ) {
            return null;
        }
        String name = instructor.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
