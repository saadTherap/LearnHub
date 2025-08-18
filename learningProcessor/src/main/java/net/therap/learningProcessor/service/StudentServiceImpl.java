package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.cache.support.CacheInvalidationUtil;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.exception.ResourceNotFoundException;
import net.therap.learningProcessor.mapper.StudentMapper;
import net.therap.learningProcessor.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final CacheInvalidationUtil cacheInvalidationUtil;

    @Override
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("error.student.notFound", id));
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentDto dto) {
        Student entity = studentMapper.toEntity(dto);
        Student savedEntity = studentRepository.save(entity);
        cacheInvalidationUtil.invalidateCachesAfterCommit(String.valueOf(savedEntity.getId()), CacheConstants.STUDENTS);

        return studentMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public StudentDto updateStudent(StudentDto studentDto) {
        Student existingStudent = studentRepository.findById(studentDto.getId()).
                orElseThrow(() -> new ResourceNotFoundException("error.student.notFound", studentDto.getId()));

        studentMapper.updateStudentFromDto(studentDto, existingStudent);
        Student updatedStudent= studentRepository.save(existingStudent);
        cacheInvalidationUtil.invalidateCachesAfterCommit(String.valueOf(updatedStudent.getId()), CacheConstants.STUDENTS);

        return studentMapper.toDto(updatedStudent);
    }

    @Override
    @Transactional
    public boolean deleteStudent(Long id) {
        Student student = studentRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("error.student.notFound", id));

        student.setDeleted(true);
        studentRepository.save(student);
        cacheInvalidationUtil.invalidateCachesAfterCommit(String.valueOf(student.getId()), CacheConstants.STUDENTS);

        return true;
    }
}