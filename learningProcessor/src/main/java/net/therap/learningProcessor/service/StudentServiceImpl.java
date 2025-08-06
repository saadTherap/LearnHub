package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.mapper.StudentMapper;
import net.therap.learningProcessor.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author avidewan
 * @since 7/27/25
 */

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

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
                .orElse(null);
    }

    @Override
    public StudentDto createStudent(StudentDto dto) {
        Student entity = studentMapper.toEntity(dto);
        Student savedEntity = studentRepository.save(entity);

        return studentMapper.toDto(savedEntity);
    }

    @Override
    public StudentDto updateStudent(StudentDto studentDto) {
        Student existingStudent = studentRepository.findById(studentDto.getId()).orElse(null);

        if(Objects.isNull(existingStudent)) {
            return null;
        }

        studentMapper.updateStudentFromDto(studentDto, existingStudent);
        Student updatedStudent= studentRepository.save(existingStudent);

        return studentMapper.toDto(updatedStudent);
    }


    @Override
    public boolean deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElse(null);

        if(Objects.isNull(student)) {
            return false;
        }

        student.setDeleted(true);
        studentRepository.save(student);

        return true;
    }
}