package net.therap.learningProcessor.mapper;

import net.therap.learningProcessor.dto.submission.StudentSubmissionDto;
import net.therap.learningProcessor.entity.StudentSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author avidewan
 * @since 8/7/25
 */
@Mapper(componentModel = "spring")
public interface StudentSubmissionMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(expression = "java(submission.getStudent().getFirstName() + \" \" + submission.getStudent().getLastName())", target = "studentName")
    StudentSubmissionDto toDto(StudentSubmission submission);

    @Mapping(source = "studentId", target = "student.id")
    StudentSubmission toEntity(StudentSubmissionDto dto);
}