package net.therap.learningProcessor.mapper;

import net.therap.learningProcessor.dto.submission.StudentSubmissionDto;
import net.therap.learningProcessor.entity.StudentSubmission;
import org.mapstruct.Mapper;

/**
 * @author avidewan
 * @since 8/7/25
 */
@Mapper(componentModel = "spring")
public interface StudentSubmissionMapper {

    StudentSubmissionDto toDto(StudentSubmission submission);

    StudentSubmission toEntity(StudentSubmissionDto dto);
}