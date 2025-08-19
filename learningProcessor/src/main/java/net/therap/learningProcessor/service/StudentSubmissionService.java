package net.therap.learningProcessor.service;

import net.therap.learningProcessor.dto.StoredFileDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.dto.content.submission.StudentSubmissionDto;

import java.util.List;
import java.util.Optional;

/**
 * @author avidewan
 * @since 8/7/25
 */
public interface StudentSubmissionService {

    StudentSubmissionDto submit(StudentDto studentDto, Long contentId, StoredFileDto storedFileDto);

    List<StudentSubmissionDto> getAllByStudentId(Long studentId);

    List<StudentSubmissionDto> getAllByContentId(Long contentId);

    List<StudentSubmissionDto> getLatestSubmissionPerStudentByContentId(Long contentId);

    List<StudentSubmissionDto> getAllByStudentIdAndContentId(Long studentId, Long contentId);

    Optional<StudentSubmissionDto> getLatestByStudentIdAndContentId(Long studentId, Long contentId);
}
