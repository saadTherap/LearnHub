package net.therap.learningProcessor.mapper;

import javax.annotation.processing.Generated;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.entity.Student;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:50+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class StudentCourseProgressMapperImpl implements StudentCourseProgressMapper {

    @Override
    public StudentCourseProgressDto toDto(Student student, CourseDetailWithProgressDto course, double progress) {
        if ( student == null && course == null ) {
            return null;
        }

        Long studentId = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        if ( student != null ) {
            studentId = student.getId();
            firstName = student.getFirstName();
            lastName = student.getLastName();
            email = student.getEmail();
        }
        Long courseId = null;
        String courseName = null;
        if ( course != null ) {
            courseId = course.getId();
            courseName = course.getName();
        }
        double progress1 = 0.0d;
        progress1 = progress;

        StudentCourseProgressDto studentCourseProgressDto = new StudentCourseProgressDto( studentId, firstName, lastName, email, courseId, courseName, progress1 );

        return studentCourseProgressDto;
    }
}
