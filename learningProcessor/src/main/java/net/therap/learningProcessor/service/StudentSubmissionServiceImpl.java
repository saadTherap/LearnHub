package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.dto.StoredFileDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.dto.content.submission.StudentSubmissionDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.entity.StudentSubmission;
import net.therap.learningProcessor.mapper.StudentMapper;
import net.therap.learningProcessor.mapper.StudentSubmissionMapper;
import net.therap.learningProcessor.repository.StudentSubmissionRepository;
import net.therap.learningProcessor.service.StudentSubmissionService;
import net.therap.learningProcessor.validator.StudentValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author avidewan
 * @since 8/7/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentSubmissionServiceImpl implements StudentSubmissionService {

    private final StudentSubmissionRepository submissionRepository;
    private final StudentSubmissionMapper submissionMapper;
    private final StudentMapper studentMapper;

    private final StudentValidator studentValidator;

    @Override
    @Transactional
    public StudentSubmissionDto submit(StudentDto studentDto,
                                       Long contentId,
                                       StoredFileDto storedFileDto) {

        studentValidator.validateStudentExists(studentDto.getId());

        Student student = studentMapper.toEntity(studentDto);

        StudentSubmission submission = buildSubmission(student, contentId, storedFileDto);
        submission = submissionRepository.save(submission);

        return submissionMapper.toDto(submission);
    }

    @Override
    public List<StudentSubmissionDto> getAllByStudentId(Long studentId) {
        studentValidator.validateStudentExists(studentId);

        return submissionRepository.findAllByStudentIdOrderBySubmittedAtDesc(studentId)
                .stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentSubmissionDto> getAllByContentId(Long contentId) {

        return submissionRepository.findAllByContentIdOrderBySubmittedAtDesc(contentId)
                .stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentSubmissionDto> getAllByStudentIdAndContentId(Long studentId, Long contentId) {
        studentValidator.validateStudentExists(studentId);

        return submissionRepository.findAllByStudentIdAndContentIdOrderBySubmittedAtDesc(studentId, contentId)
                .stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StudentSubmissionDto> getLatestByStudentIdAndContentId(Long studentId, Long contentId) {
        studentValidator.validateStudentExists(studentId);

        return submissionRepository.findFirstByStudentIdAndContentIdOrderBySubmittedAtDesc(studentId, contentId)
                .map(submissionMapper::toDto);
    }

    @Override
    public List<StudentSubmissionDto> getLatestSubmissionPerStudentByContentId(Long contentId) {

        return submissionRepository.findLatestSubmissionPerStudentByContentId(contentId)
                .stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    private StudentSubmission buildSubmission(Student student,
                                              Long contentId,
                                              StoredFileDto fileDto) {

        StudentSubmission submission = new StudentSubmission();

        submission.setStudent(student);
        submission.setContentId(contentId);
        submission.setFormId(fileDto.getFormId());
        submission.setDownloadUrl(fileDto.getDownloadUrl());
        submission.setOriginalFileName(fileDto.getOriginalFilename());
        submission.setContentType(fileDto.getContentType());
        submission.setUploaderEmail(fileDto.getUploaderEmail());
        submission.setSubmittedAt(fileDto.getUploadTime());

        return submission;
    }
}