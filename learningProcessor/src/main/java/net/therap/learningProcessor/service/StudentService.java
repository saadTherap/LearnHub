package net.therap.learningProcessor.service;

import net.therap.learningProcessor.dto.StudentDto;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
public interface StudentService {

    List<StudentDto> getAllStudents();

    StudentDto getStudentById(Long id);

    StudentDto createStudent(StudentDto dto);

    StudentDto updateStudent(StudentDto dto);

    StudentDto getStudentByEmail(String email);

    boolean deleteStudent(Long id);

    void hardDelete(Long id);
}