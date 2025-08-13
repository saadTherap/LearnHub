package net.therap.learningProcessor.service.impl;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.FileClient;
import net.therap.learningProcessor.dto.StoredFileDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.dto.content.submission.StudentSubmissionDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.entity.StudentSubmission;
import net.therap.learningProcessor.mapper.StudentMapper;
import net.therap.learningProcessor.mapper.StudentSubmissionMapper;
import net.therap.learningProcessor.repository.StudentSubmissionRepository;
import net.therap.learningProcessor.service.StudentSubmissionService;
import net.therap.learningProcessor.validator.CourseValidator;
import net.therap.learningProcessor.validator.StudentValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private final FileClient fileClient;
    private final StudentSubmissionRepository submissionRepository;

    private final StudentSubmissionMapper submissionMapper;
    private final StudentMapper studentMapper;

    private final StudentValidator studentValidator;
    private final CourseValidator courseValidator;

    @Override
    @Transactional
    public StudentSubmissionDto submit(StudentDto studentDto,
                                       Long contentId,
                                       MultipartFile file) {

        studentValidator.validateStudentExists(studentDto.getId());
        courseValidator.validateContentExists(contentId);

        StoredFileDto storedFileDto = fileClient.uploadFile(file);

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
        courseValidator.validateContentExists(contentId);

        return submissionRepository.findAllByContentIdOrderBySubmittedAtDesc(contentId)
                .stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentSubmissionDto> getAllByStudentIdAndContentId(Long studentId, Long contentId) {
        studentValidator.validateStudentExists(studentId);
        courseValidator.validateContentExists(contentId);

        return submissionRepository.findAllByStudentIdAndContentIdOrderBySubmittedAtDesc(studentId, contentId)
                .stream()
                .map(submissionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StudentSubmissionDto> getLatestByStudentIdAndContentId(Long studentId, Long contentId) {
        studentValidator.validateStudentExists(studentId);
        courseValidator.validateContentExists(contentId);

        return submissionRepository.findFirstByStudentIdAndContentIdOrderBySubmittedAtDesc(studentId, contentId)
                .map(submissionMapper::toDto);
    }

    @Override
    public List<StudentSubmissionDto> getLatestSubmissionPerStudentByContentId(Long contentId) {
        courseValidator.validateContentExists(contentId);

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
        submission.setFileId(fileDto.getId());
        submission.setDownloadUrl(fileDto.getDownloadUrl());
        submission.setOriginalFileName(fileDto.getOriginalFilename());
        submission.setSubmittedAt(LocalDateTime.now());

        return submission;
    }
}