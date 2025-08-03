package net.therap.learningProcessor.controller;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.service.HazelcastCacheService;
import net.therap.learningProcessor.service.StudentService;
import net.therap.learningProcessor.validator.group.OnCreate;
import net.therap.learningProcessor.validator.group.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author avidewan
 * @since 7/27/25
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Validated
public class StudentController {

    private final StudentService studentService;
    private final HazelcastCacheService hazelcastCacheService;

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {

        StudentDto cachedStudent = hazelcastCacheService.get(CacheConstants.STUDENTS, id);
        if (cachedStudent != null) {
            return ResponseEntity.ok(cachedStudent);
        }

        // Fetch from service/db
        StudentDto student = studentService.getStudentById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        hazelcastCacheService.put(CacheConstants.STUDENTS, id, student);

        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Validated(OnCreate.class) @RequestBody StudentDto studentDto) {
        StudentDto created = studentService.createStudent(studentDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id,
                                                    @Validated(OnUpdate.class) @RequestBody StudentDto studentDto) {
        studentDto.setId(id);
        StudentDto updatedStudent = studentService.updateStudent(studentDto);

        if (Objects.isNull(updatedStudent)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        boolean deleted = studentService.deleteStudent(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}