package net.therap.learningProcessor.mapper;

import net.therap.learningProcessor.dto.CourseDetailDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author avidewan
 * @since 7/28/25
 */
@Mapper(componentModel = "spring")
public interface StudentCourseProgressMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.firstName", target = "firstName")
    @Mapping(source = "student.lastName", target = "lastName")
    @Mapping(source = "student.email", target = "email")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.name", target = "courseName")
    @Mapping(source = "progress", target = "progress")
    StudentCourseProgressDto toDto(Student student, CourseDetailDto course, double progress);
}