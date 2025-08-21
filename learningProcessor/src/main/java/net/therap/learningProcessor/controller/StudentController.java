package net.therap.learningProcessor.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.service.AuthorizationService;
import net.therap.learningProcessor.service.StudentService;
import net.therap.learningProcessor.util.StudentUtil;
import net.therap.learningProcessor.validator.group.OnCreate;
import net.therap.learningProcessor.validator.group.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Slf4j
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Validated
public class StudentController {

    private final StudentService studentService;
    private final HazelcastCacheService hazelcastCacheService;
    private final AuthorizationService authorizationService;
    private final StudentUtil studentUtil;

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents(HttpServletRequest request) {
        authorizationService.authorize(AccessLevel.INSTRUCTOR_ONLY, request);

        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id, HttpServletRequest request) {

        log.info("[Get] Student, {}", id);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OR_STUDENT_WITH_ID,  Map.of("studentId", id), request);

        log.info("Authorized");

        StudentDto cachedStudent = hazelcastCacheService.get(CacheConstants.STUDENTS, id);
        if (cachedStudent != null) {
            return ResponseEntity.ok(cachedStudent);
        }

        StudentDto student = studentService.getStudentById(id);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        hazelcastCacheService.put(CacheConstants.STUDENTS, id, student);

        log.info("Response: {}", student);

        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Validated(OnCreate.class) @RequestBody StudentDto studentDto) {
        StudentDto created = studentService.createStudent(studentDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id,
                                                    @Validated(OnUpdate.class) @RequestBody StudentDto studentDto,
                                                    HttpServletRequest request) {

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID,  Map.of("studentId", id), request);

        studentDto.setId(id);
        StudentDto updatedStudent = studentService.updateStudent(studentDto);

        if (Objects.isNull(updatedStudent)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id,
                                              HttpServletRequest request) {

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID,  Map.of("studentId", id), request);

        boolean deleted = studentService.deleteStudent(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable String email, HttpServletRequest request) {

        StudentDto student = studentService.getStudentByEmail(email);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }

    @GetMapping("/fromToken")
    public ResponseEntity<StudentDto> getStudentFromToken(HttpServletRequest request) {

        StudentDto student = studentUtil.getStudentFromRequest(request);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }


}